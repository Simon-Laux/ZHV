import marked from 'marked';
import './styles.scss';

const LocalStorageID = "zhv-internal-markdown-reader"

document.getElementsByTagName('body')[0].innerHTML = `<div id="themes"></div><div id="content"></div>`

const Themes = [
    ["light theme", ""],
    ["warm theme", "warm"],
    ["dark theme", "dark"],
    ["hacker theme", "hacker"],
    ["cyber theme", "cyber"],
    ["retro theme", "retro"],
]

const ThemeManager = document.createElement("select")

document.getElementById('themes').appendChild(ThemeManager)

Themes.forEach(([name, className]) => {
    const option = document.createElement("option")
    option.innerText = name
    option.value = className
    ThemeManager.appendChild(option)
})

ThemeManager.addEventListener('change', ({ target: { value } }) => {
    document.getElementsByTagName('html')[0].classList.value = value
    localStorage.setItem(LocalStorageID + "-theme", value)
})

//Load theme
document.getElementsByTagName('html')[0].classList.value =
    ThemeManager.value = localStorage.getItem(LocalStorageID + "-theme") || ""

const markdown_content = typeof zhv !== "undefined" ? zhv.getMarkdown() : "## ONLY WORKS IN ZHV"

/* Test data, uncoment to use *//*
const markdown_content = `
# ZHV - Zipped HTML Viewer
This is an Android app.

Opens files with the extention \`.html.zip\` or \`.htmlzip\` and displays their content in a webview.
The app can also open plain html and even markdown files.

It doesn't allow loading resources from the web. Assets must be inline because it only loads from the index html file for now.

## Zipped HTML format

Has extention \`.html.zip\`(for backwards compatibility) or \`.htmlzip\` (that automatic renaming don't break the extention).
It is an zip file with following content:
\`\`\`
example.html.zip
└── index.html
\`\`\`

## Special JavaScript

The app exposes the global \`zhv\` object, it can currently be used to check if the site was opened in ZHV and get the version code of ZHV for new functionality that may come in the future.

\`\`\`ts
zhv : {
 getVersion:()=>number
}
\`\`\`

## Note Regarding Localstorage

If you use local storage please prefix your variables with an unique prefix (your webapp name + random uuid should do it) to avoid collision with other web apps.
Also don't use \`clear()\` as it clears everything for every webapp.

## Building ZHV
Debug build:

run \`./gradlew build\` on linux or \`gradlew.bat build\` on windows

[link](#)

table | table | t
------|-------|---
t3    | \`t\` | **7&**

`
*/

document.getElementById("content").innerHTML = marked(markdown_content)
