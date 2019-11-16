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

document.getElementById("content").innerHTML = marked(markdown_content)
