const expected = document.getElementById("expected");
const actual = document.getElementById("actual");

const formatExpected = document.getElementById("formatExpected");
const formatActual = document.getElementById("formatActual");

const compareButton = document.getElementById("compare");
const status = document.getElementById("status");
const result = document.getElementById("result");

const ignoreArrayOrder =
    document.getElementById("ignoreArrayOrder");

const nullMissingEqual =
    document.getElementById("nullMissingEqual");

const numericTolerance =
    document.getElementById("numericTolerance");

const ignoredPaths =
    document.getElementById("ignoredPaths");

const unorderedArrayPaths =
    document.getElementById("unorderedArrayPaths");

const expectedCard = document.getElementById("expectedCard");
const actualCard = document.getElementById("actualCard");

const expectedFile = document.getElementById("expectedFile");
const actualFile = document.getElementById("actualFile");

const loadExpected = document.getElementById("loadExpected");
const loadActual = document.getElementById("loadActual");

const themeToggle = document.getElementById("themeToggle");
const resetExample = document.getElementById("resetExample");

const DEFAULT_EXPECTED = `{
  "name": "Alice",
  "age": 30,
  "active": true
}`;

const DEFAULT_ACTUAL = `{
  "name": "Bob",
  "age": 30,
  "active": true
}`;

const clearExpected = document.getElementById("clearExpected");
const clearActual = document.getElementById("clearActual");

function formatJson(textarea) {
    try {
        const parsed = JSON.parse(textarea.value);
        textarea.value = JSON.stringify(parsed, null, 2);
        return true;
    } catch (error) {
        result.textContent = "Invalid JSON: " + error.message;
        result.classList.add("error");
        return false;
    }
}

function applyTheme(theme) {
    document.documentElement.dataset.theme = theme;

    const dark = theme === "dark";

    themeToggle.textContent = dark ? "☀" : "☾";
    themeToggle.setAttribute(
        "aria-label",
        dark ? "Switch to light mode" : "Switch to dark mode"
    );
    themeToggle.title =
        dark ? "Switch to light mode" : "Switch to dark mode";
}

const savedTheme = localStorage.getItem("json-differ-theme");

applyTheme(savedTheme === "light" ? "light" : "dark");

themeToggle.addEventListener("click", () => {
    const current =
        document.documentElement.dataset.theme;

    const next =
        current === "dark" ? "light" : "dark";

    applyTheme(next);
    localStorage.setItem("json-differ-theme", next);
});

async function loadJsonFile(file, textarea) {
    if (!file) {
        return;
    }

    if (!file.name.toLowerCase().endsWith(".json")) {
        result.textContent = "Please select a .json file.";
        result.classList.add("error");
        return;
    }

    try {
        textarea.value = await file.text();

        result.classList.remove("error");
        status.textContent = `Loaded ${file.name}.`;
    } catch (error) {
        result.textContent = `Could not read ${file.name}.`;
        result.classList.add("error");
    }
}

function enableFileDrop(card, textarea) {
    card.addEventListener("dragover", (event) => {
        event.preventDefault();
        card.classList.add("drag-over");
    });

    card.addEventListener("dragleave", () => {
        card.classList.remove("drag-over");
    });

    card.addEventListener("drop", async (event) => {
        event.preventDefault();
        card.classList.remove("drag-over");

        const file = event.dataTransfer.files[0];

        await loadJsonFile(file, textarea);
    });
}

formatExpected.addEventListener("click", () => {
    formatJson(expected);
});

formatActual.addEventListener("click", () => {
    formatJson(actual);
});

try {
    await cheerpjInit({
        version: 8
    });

    status.textContent = "Loading json-differ...";

    const jarUrl =
        new URL("./json-differ-demo.jar", window.location.href);

    const jarPath =
        "/app" + jarUrl.pathname;

    const lib =
        await cheerpjRunLibrary(jarPath);

    const DemoBridge =
        await lib.io.github.nigalranieri.jsondiffer.demo.DemoBridge;

    const JavaString =
        await lib.java.lang.String;

    compareButton.disabled = false;
    status.textContent = "Ready.";

    compareButton.addEventListener("click", async () => {
        result.classList.remove("error");

        try {
            status.textContent = "Comparing...";
            compareButton.disabled = true;

            const expectedJava =
                await new JavaString(expected.value);

            const actualJava =
                await new JavaString(actual.value);

            const ignoredPathsJava =
                await new JavaString(ignoredPaths.value);

            const unorderedArrayPathsJava =
                await new JavaString(unorderedArrayPaths.value);

            const numericToleranceJava =
                await new JavaString(numericTolerance.value);

            const grouped =
                document.querySelector(
                    'input[name="resultFormat"]:checked'
                ).value === "grouped";

            const comparison =
                await DemoBridge.compare(
                    expectedJava,
                    actualJava,
                    ignoredPathsJava,
                    ignoreArrayOrder.checked,
                    unorderedArrayPathsJava,
                    nullMissingEqual.checked,
                    numericToleranceJava,
                    grouped
                );

            const output =
                await comparison.toString();

            result.textContent = output;

            if (output.startsWith("ERROR:")) {
                result.classList.add("error");
                status.textContent = "Comparison failed.";
            } else {
                status.textContent = "Done.";
            }

        } catch (error) {
            status.textContent = "Comparison failed.";
            result.textContent =
                "Unexpected browser runtime error. Check the developer console.";

            result.classList.add("error");
            console.error("CheerpJ error:", error);

        } finally {
            compareButton.disabled = false;
        }
    });

} catch (error) {
    status.textContent = "Failed to initialize browser Java runtime.";

    result.textContent =
        "The json-differ runtime could not be loaded. Check the developer console.";

    result.classList.add("error");
    console.error("CheerpJ initialization error:", error);
}

function enableTabIndentation(textarea) {
    textarea.addEventListener("keydown", (event) => {
        if (event.key !== "Tab") {
            return;
        }

        event.preventDefault();

        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        const indent = "  ";

        textarea.setRangeText(
            indent,
            start,
            end,
            "end"
        );
    });
}

function updateUnorderedPathState() {
    unorderedArrayPaths.disabled = ignoreArrayOrder.checked;
}

ignoreArrayOrder.addEventListener(
    "change",
    updateUnorderedPathState
);

loadExpected.addEventListener("click", () => {
    expectedFile.click();
});

loadActual.addEventListener("click", () => {
    actualFile.click();
});

expectedFile.addEventListener("change", async () => {
    await loadJsonFile(expectedFile.files[0], expected);
    expectedFile.value = "";
});

actualFile.addEventListener("change", async () => {
    await loadJsonFile(actualFile.files[0], actual);
    actualFile.value = "";
});

enableFileDrop(expectedCard, expected);
enableFileDrop(actualCard, actual);

updateUnorderedPathState();

resetExample.addEventListener("click", () => {
    expected.value = DEFAULT_EXPECTED;
    actual.value = DEFAULT_ACTUAL;

    ignoredPaths.value = "";
    unorderedArrayPaths.value = "";
    numericTolerance.value = "";

    ignoreArrayOrder.checked = false;
    nullMissingEqual.checked = false;

    document.querySelector(
        'input[name="resultFormat"][value="traversal"]'
    ).checked = true;

    updateUnorderedPathState();

    result.classList.remove("error");
    result.textContent = "Waiting for comparison...";
    status.textContent = "Ready.";
});

clearExpected.addEventListener("click", () => {
    expected.value = "";
    expected.focus();
});

clearActual.addEventListener("click", () => {
    actual.value = "";
    actual.focus();
});

enableTabIndentation(expected);
enableTabIndentation(actual);