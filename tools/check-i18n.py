#!/usr/bin/env python3
"""Offline localization catalog checks. Android resource references are verified by aapt/javac."""
from __future__ import annotations

import argparse
from collections import Counter
from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET

FORMAT = re.compile(r'%(?:([1-9][0-9]*)\$)?([-#+ 0,(]*)([0-9]*)(?:\.([0-9]+))?([a-zA-Z%])')
PLURAL_QUANTITIES = frozenset(('zero', 'one', 'two', 'few', 'many', 'other'))


def placeholders(value: str) -> Counter:
    result = Counter()
    pos = 0
    while '%' in value[pos:]:
        pos = value.index('%', pos)
        match = FORMAT.match(value, pos)
        if match is None:
            raise ValueError('malformed format placeholder: ' + value)
        index, _flags, _width, _precision, kind = match.groups()
        if kind not in ('%', 'n'):
            if index is None:
                raise ValueError('use positional placeholders (%1$s, %2$d): ' + value)
            result[(int(index), kind.lower())] += 1
        pos = match.end()
    return result


def load(path: Path) -> dict[str, ET.Element]:
    raw = path.read_text(encoding='utf-8')
    if '<!DOCTYPE' in raw or '<!ENTITY' in raw:
        raise ValueError('DTD/entities are not allowed: ' + str(path))

    root = ET.fromstring(raw)
    if root.tag != 'resources':
        raise ValueError('root must be <resources>: ' + str(path))

    entries: dict[str, ET.Element] = {}
    for node in root:
        if not isinstance(node.tag, str):
            continue
        name = node.get('name', '')
        if not re.fullmatch(r'[a-z][a-z0-9_]*', name):
            raise ValueError('invalid resource name: ' + name)
        if name in entries:
            raise ValueError('duplicate resource: ' + name)
        if node.tag not in ('string', 'string-array', 'plurals'):
            raise ValueError('unsupported catalog element: ' + node.tag)
        entries[name] = node
    return entries


def values(node: ET.Element) -> list[str]:
    name = node.get('name', '')
    if node.tag == 'string':
        if len(node):
            raise ValueError('markup is not supported in this UI catalog: ' + name)
        return [node.text or '']

    if not len(node) or any(item.tag != 'item' or len(item) for item in node):
        raise ValueError('invalid item list: ' + name)

    if node.tag == 'plurals':
        quantities = [item.get('quantity', '') for item in node]
        invalid = [quantity for quantity in quantities if quantity not in PLURAL_QUANTITIES]
        if invalid:
            raise ValueError('invalid plural quantity in ' + name + ': ' + invalid[0])
        if len(set(quantities)) != len(quantities):
            raise ValueError('duplicate plural quantity: ' + name)
        if 'other' not in quantities:
            raise ValueError('plural has no other item: ' + name)

    return [item.text or '' for item in node]


def validate_catalog(entries: dict[str, ET.Element]) -> None:
    for node in entries.values():
        for value in values(node):
            placeholders(value)


def compare(base: dict[str, ET.Element], locale: dict[str, ET.Element]) -> None:
    extra = set(locale) - set(base)
    if extra:
        raise ValueError('translated keys have no default: ' + ', '.join(sorted(extra)))

    for name, translated in locale.items():
        original = base[name]
        if original.get('translatable') == 'false':
            raise ValueError('non-translatable key repeated: ' + name)
        if original.tag != translated.tag:
            raise ValueError('resource type changed: ' + name)

        original_values = values(original)
        translated_values = values(translated)
        if original.tag == 'string-array' and len(original_values) != len(translated_values):
            raise ValueError('array length changed: ' + name)

        if original.tag == 'plurals':
            fallback = next(
                (item for item in original if item.get('quantity') == 'other'),
                None)
            if fallback is None:
                raise ValueError('default plural has no other item: ' + name)
            by_quantity = {item.get('quantity'): item for item in original}
            pairs = []
            for item in translated:
                source = by_quantity.get(item.get('quantity'), fallback)
                pairs.append((source.text or '', item.text or ''))
        else:
            pairs = zip(original_values, translated_values)

        for source, target in pairs:
            if not target.strip():
                raise ValueError('empty translation: ' + name)
            if placeholders(source) != placeholders(target):
                raise ValueError('placeholder mismatch: ' + name)


def check_tree(root: Path) -> tuple[int, int, int, int]:
    base = load(root / 'res/values/strings.xml')
    validate_catalog(base)
    locales = sorted((root / 'res').glob('values-*/strings.xml'))
    for path in locales:
        locale = load(path)
        validate_catalog(locale)
        compare(base, locale)

    counts = Counter(node.tag for node in base.values())
    return counts['string'], counts['plurals'], counts['string-array'], len(locales)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        'root',
        nargs='?',
        type=Path,
        default=Path(__file__).resolve().parent.parent)
    args = parser.parse_args()

    try:
        counts = check_tree(args.root)
    except (ValueError, OSError, ET.ParseError) as error:
        print('i18n catalog FAIL: ' + str(error), file=sys.stderr)
        return 1

    print(
        'i18n catalog PASS: %d strings, %d plurals, %d arrays, %d locale catalogs'
        % counts)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
