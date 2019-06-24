# ZHV - Zipped HTML Viewer
This is an Android app.

Opens files with the extention `.html.zip` or `.htmlzip` and displays their content in a webview.
The app can also open plain html files. 

It doesn't allow loading resources from the web. Assets must be inline because it only loads from the index html file for now.

## Zipped HTML format

Has extention `.html.zip`(for backwards compatibility) or `.htmlzip` (that automatic renaming don't break the extention).
It is an zip file with following content:
```
example.html.zip
└── index.html
```

## Special JavaScript

The app exposes the global `zhv` object, it can currently be used to check if the site was opened in ZHV and get the version code of ZHV for new functionality that may come in the future.

```ts
zhv : {
 getVersion:()=>number
}
```

## Note Regarding Localstorage

If you use local storage please prefix your variables with an unique prefix (your webapp name + random uuid should do it) to avoid collision with other web apps.
Also don't use `clear()` as it clears everything for every webapp.

## Building ZHV
Debug build:

run `./gradlew build` on linux or `gradlew.bat build` on windows