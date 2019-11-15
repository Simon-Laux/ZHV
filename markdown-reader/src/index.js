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

const markdown_content = `

## Hello World

Hi I'm **bold** \`inline-code\`

\`\`\`
say.hi()
\`\`\`

*Kursivschrift*

* Aufzählung 1 (Leerzeichen nach dem Stern nicht vergessen)

* Aufzählung 2

1. Schritt 1

2. Schritt 2

* Erste Ebene dieser Liste.

* Um eine zweite Ebene zu erstellen, geben Sie zwei Leerzeichen vor dem Stern bzw. der Zahl ein.

# Überschriftsebene 1 (Leerzeichen nach #)

## Überschriftsebene 2

### Überschriftsebene 3

Sie können bis zu sechs Überschriftsebenen hinzufügen.

> Blockzitate müssen mit einer Leerzeile beginnen und enden

> Jede Zeile des Zitats beginnt mit einer rechten spitzen Klammer und einem Leerzeichen

Beispiel für \`Inlinecode\`.

![Optionaler Alternativtext, falls sich das Bild nicht laden lässt](http://www.sampleurl.com/logo.png)

[Anzeigetext für Link](http://www.sampleurl.com)

[![Alt-Text](imageurl)](linkurl)

Wir gingen in Ticket #61 darauf ein.

---

 	

Zeile 1

 

&nbsp;

Zeile 2

 

&nbsp;

Colons can be used to align columns.

| Tables        | Are           | Cool  |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
| col 2 is      | centered      |   $12 |
| zebra stripes | are neat      |    $1 |

There must be at least 3 dashes separating each header cell.
The outer pipes (|) are optional, and you don't need to make the 
raw Markdown line up prettily. You can also use inline Markdown.

Markdown | Less | Pretty
--- | --- | ---
*Still* | \`renders\` | **nicely**
1 | 2 | 3

`


document.getElementById("content").innerHTML = marked(markdown_content)
