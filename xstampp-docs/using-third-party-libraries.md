Using third-party libraries in the XSTAMPP front-end
===================================

:house:[Home](README.md)

Installation
--------------

In order to make an external .js-file available for use in the frontend of XSTAMPP,
they have to be referenced inside `index.html`, which is located in `xstampp-angular\src\`. There are two ways of doing that.

1. You can add a link to a library available on an external website by typing the following command inside the HTML-header:
`<script src="https://nameOfWebsite.com/nameOfLibrary.js"></script>`.
Note that in some cases these kinds of cross-references are blocked by the browser, therefore we __highly recommend to use the second option.__
2. You can copy the needed library into the project's `assets`-folder located in `xstampp-angular\src\assets\`. Then, you can include it inside `index.html` by simply adding the line: `<script src="assets/nameOfLibrary.js"></script>` to the header.

Using the libraries
--------------------------

When building the front-end, the linked libraries are not read, which means that trying to use functions or types defined inside them will cause the compilation to fail. What you want to do is put all code referencing these libraries inside a string and then use the following command: `eval(nameOfTheString);`
This command will also return any object returned by the code inside the string. _Hint_: Make it readable by using multiline strings (add a backslash to the end of the line)!

Example
---------------------------

The following example creates a `Parser`-object from the `nearley`-module defined inside a third party library. It then calls its method `feed()` and as parameter gives the previously defined string `rule` by using `"'+ rule + '"`. Finally, the first entry of the array `results` is called. Don't use `return` here, as we're not inside a function. Simply type what you want `eval()` to return.

`let command = 'var parser = new nearley.Parser(grammar.ParserRules, grammar.ParserStart);\`

`parser.feed("'`

`+ rule`

`+ '");\`

`parser.results[0];';`

`let result = eval(command);`