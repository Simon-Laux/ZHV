# ZHV - Zipped HTML Viewer
This is an Android app.

Opens files with the extention `.html.zip` or `.htmlzip` and displays their content in a webview.

It doesn't allow loading resoources from the web. Assets must be inline because it only loads from the index html file for now.

## Zipped HTML format

Has extention `.html.zip`(for backwards compatibility) or `.htmlzip` (that automatic renaming don't break the extention).
It is an zip file with following content:
```
example.html.zip
└── index.html
```

## Building ZHV
Debug build:

run `./gradlew build` on linux or `gradlew.bat build` on windows