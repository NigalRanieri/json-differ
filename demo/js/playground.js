const expected = document.getElementById("expected");
const actual = document.getElementById("actual");
const config = document.getElementById("config");

const formatExpected = document.getElementById("formatExpected");
const formatActual = document.getElementById("formatActual");

const compareButton = document.getElementById("compare");
const status = document.getElementById("status");
const result = document.getElementById("result");

const expectedCard = document.getElementById("expectedCard");
const actualCard = document.getElementById("actualCard");

const expectedFile = document.getElementById("expectedFile");
const actualFile = document.getElementById("actualFile");

const loadExpected = document.getElementById("loadExpected");
const loadActual = document.getElementById("loadActual");

const clearExpected = document.getElementById("clearExpected");
const clearActual = document.getElementById("clearActual");
const clearConfig = document.getElementById("clearConfig");
const loadConfigExample = document.getElementById("loadConfigExample");

const themeToggle = document.getElementById("themeToggle");
const resetExample = document.getElementById("resetExample");

const configInfoButton = document.getElementById("configInfoButton");

const configInfoPopover = document.getElementById("configInfoPopover");

const DEFAULT_EXPECTED = `{
  "user": {
    "name": "Alice",
    "email": "ALICE@example.com",
    "score": 98.5,
    "nickname": null
  },
  "roles": [
    "admin",
    "editor"
  ],
  "metadata": {
    "requestId": "abc-123",
    "timestamp": "2026-08-27T10:00:00Z"
  }
}`;

const DEFAULT_ACTUAL = `{
  "user": {
    "name": "Alice",
    "email": "alice@example.com",
    "score": 98.7
  },
  "roles": [
    "editor",
    "admin"
  ],
  "metadata": {
    "requestId": "xyz-789",
    "timestamp": "2026-08-27T10:05:00Z"
  }
}`;

const DEFAULT_CONFIG = "";

const EXAMPLE_CONFIG = `comparison:
  ignorePaths:
    - $.metadata.requestId
    - $.metadata.timestamp

  arrayOrder:
    ignoreAt:
      - $.roles

  nullAndMissing:
    equalAt:
      - $.user.nickname

  numericTolerance:
    paths:
      $.user.score: 0.5

  ignoreCase:
    paths:
      - $.user.email

output:
  format: grouped
  columns:
    maxCellWidth: 40
`;

function formatJson(textarea) {
    try {
        const parsed = JSON.parse(textarea.value);
        textarea.value = JSON.stringify(parsed, null, 2);
        result.classList.remove("error");
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
    themeToggle.setAttribute("aria-label", dark ? "Switch to light mode" : "Switch to dark mode");
    themeToggle.title = dark ? "Switch to light mode" : "Switch to dark mode";
}

const savedTheme = localStorage.getItem("json-differ-theme");

applyTheme(savedTheme === "light" ? "light" : "dark");

themeToggle.addEventListener("click", () => {
    const current = document.documentElement.dataset.theme;
    const next = current === "dark" ? "light" : "dark";

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

function enableTabIndentation(textarea) {
    textarea.addEventListener("keydown", (event) => {
        if (event.key !== "Tab") {
            return;
        }

        event.preventDefault();

        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        const indent = "  ";

        textarea.setRangeText(indent, start, end, "end");
    });
}

function closeConfigInfo() {
    configInfoPopover.hidden = true;
    configInfoButton.setAttribute("aria-expanded", "false");
}

configInfoButton.addEventListener("click", (event) => {
    event.stopPropagation();

    const isOpen = !configInfoPopover.hidden;

    if (isOpen) {
        closeConfigInfo();
    } else {
        configInfoPopover.hidden = false;
        configInfoButton.setAttribute("aria-expanded", "true");
    }
});

configInfoPopover.addEventListener("click", (event) => {
    event.stopPropagation();
});

document.addEventListener("click", () => {
    closeConfigInfo();
});

document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
        closeConfigInfo();
    }
});

formatExpected.addEventListener("click", () => {
    formatJson(expected);
});

formatActual.addEventListener("click", () => {
    formatJson(actual);
});

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

resetExample.addEventListener("click", () => {
    expected.value = DEFAULT_EXPECTED;
    actual.value = DEFAULT_ACTUAL;
    config.value = DEFAULT_CONFIG;

    result.classList.remove("error");
    result.textContent = "Waiting for comparison...";

    if (!compareButton.disabled) {
        status.textContent = "Ready.";
    }
});

clearExpected.addEventListener("click", () => {
    expected.value = "";
    expected.focus();
});

clearActual.addEventListener("click", () => {
    actual.value = "";
    actual.focus();
});

clearConfig.addEventListener("click", () => {
    config.value = "";
    config.focus();
});

loadConfigExample.addEventListener("click", () => {
    config.value = EXAMPLE_CONFIG;
    config.focus();

    result.classList.remove("error");
    status.textContent = "Example YAML loaded.";
});

enableTabIndentation(expected);
enableTabIndentation(actual);
enableTabIndentation(config);

try {
    await cheerpjInit({
        version: 8,
    });

    status.textContent = "Loading json-differ...";

    const jarUrl = new URL("./json-differ-demo.jar", window.location.href);

    const jarPath = "/app" + jarUrl.pathname;

    const lib = await cheerpjRunLibrary(jarPath);

    const DemoBridge = await lib.io.github.nigalranieri.jsondiffer.demo.DemoBridge;

    const JavaString = await lib.java.lang.String;

    compareButton.disabled = false;
    status.textContent = "Ready.";

    compareButton.addEventListener("click", async () => {
        result.classList.remove("error");

        try {
            status.textContent = "Comparing...";
            compareButton.disabled = true;

            const expectedJava = await new JavaString(expected.value);

            const actualJava = await new JavaString(actual.value);

            const configJava = await new JavaString(config.value);

            const comparison = await DemoBridge.compare(expectedJava, actualJava, configJava);

            const output = await comparison.toString();

            result.textContent = output;

            if (output.startsWith("ERROR:")) {
                result.classList.add("error");
                status.textContent = "Comparison failed.";
            } else {
                status.textContent = "Done.";
            }
        } catch (error) {
            status.textContent = "Comparison failed.";
            result.textContent = "Unexpected browser runtime error. Check the developer console.";
            result.classList.add("error");
            console.error("CheerpJ error:", error);
        } finally {
            compareButton.disabled = false;
        }
    });
} catch (error) {
    status.textContent = "Failed to initialize browser Java runtime.";

    result.textContent = "The json-differ runtime could not be loaded. Check the developer console.";

    result.classList.add("error");
    console.error("CheerpJ initialization error:", error);
}
