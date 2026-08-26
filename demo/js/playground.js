const expected = document.getElementById("expected");
const actual = document.getElementById("actual");

const formatExpected = document.getElementById("formatExpected");
const formatActual = document.getElementById("formatActual");

const compareButton = document.getElementById("compare");
const status = document.getElementById("status");
const result = document.getElementById("result");

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

            const comparison =
                await DemoBridge.compare(
                    expectedJava,
                    actualJava
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

enableTabIndentation(expected);
enableTabIndentation(actual);