const basePath = "E:/Memory";
let currentdirectory = "E:/Memory/aadi";
let forwardStack = [];

// Load items into the left pane
async function listFiles() {
  try {
    const res = await fetch(`http://localhost:8080/api/file/list?path=${encodeURIComponent(basePath)}`);
    const data = await res.json();

    const leftDiv = document.getElementsByClassName("left")[0];

    leftDiv.innerHTML = `
      <div class="list">
        ${data.folders.map(folder => `
          <div class="listf" data-folder="${folder}">
            <img src="images/folder.svg" alt="folder" class="invert" width="60px">
            <p>${folder}</p>
          </div>
        `).join("")}
        ${data.files.map(file => `
          <div class="listf">
            <img src="images/file.svg" alt="file" class="invert" width="60px">
            <p>${file}</p>
          </div>
        `).join("")}
      </div>
    `;
  } catch (err) {
    console.error("Error listing files (left pane):", err);
    alert("Error loading left pane.");
  }
}

// Load items into the right pane
async function listFilesRight(path) {
  try {
    const res = await fetch(`http://localhost:8080/api/file/list?path=${encodeURIComponent(path)}`);
    const data = await res.json();

    const rightDiv = document.getElementsByClassName("right")[0];

    rightDiv.innerHTML = `
      <b>Current_folder : ${path}</b>
      <div class="list2">
        ${data.folders.map(folder => `
          <div class="listf2" onclick="navigateToFolder('${path}/${folder}')">
            <img src="images/folder.svg" alt="folder" class="invert" width="150px">
            <div class="text">
              <p>${folder}</p>
            </div>
          </div>
        `).join("")}
        ${data.files.map(file => `
          <div class="listf2">
            <img src="images/file.svg" alt="file" class="invert" width="150px">
            <div class="text2">
              <p>${file}</p>
            </div>
          </div>
        `).join("")}
      </div>
    `;
  } catch (err) {
    console.error("Error listing files (right pane):", err);
    alert("Error loading right pane.");
  }
}

// Navigate to a new folder
async function navigateToFolder(path) {
  if (path !== currentdirectory) {
    forwardStack = [];
    currentdirectory = path;
    await listFilesRight(currentdirectory);
  }
}

// Back button
document.getElementById("back").onclick = async () => {
  if (currentdirectory === basePath) {
    alert("Already at the root folder.");
    return;
  }

  forwardStack.push(currentdirectory);

  const parts = currentdirectory.split("/");
  parts.pop();
  currentdirectory = parts.join("/");

  await listFilesRight(currentdirectory);
};

// Forward button
document.getElementById("forward").addEventListener("click", async () => {
  if (forwardStack.length === 0) {
    alert("No forward history.");
    return;
  }

  const nextPath = forwardStack.pop();
  currentdirectory = nextPath;
  await listFilesRight(currentdirectory);
});

// Create File
async function createFile() {
  const name = prompt("Enter file name:");
  if (!name || name.trim() === "") return alert("File name cannot be empty.");

  try {
    const res = await fetch("http://localhost:8080/api/file/create", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams({ path: currentdirectory, name: name.trim() })
    });

    const result = await res.text();
    alert(result);
    listFilesRight(currentdirectory);
  } catch (err) {
    console.error("Error creating file:", err);
    alert("Failed to create file.");
  }
}

// Create Folder
async function createFolder() {
  const name = prompt("Enter folder name:");
  if (!name || name.trim() === "") return alert("Folder name cannot be empty.");

  try {
    const res = await fetch("http://localhost:8080/api/file/folder/create", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams({ parent: currentdirectory, name: name.trim() })
    });

    const result = await res.text();
    if (!res.ok) throw new Error(result);
    alert(result);
    listFilesRight(currentdirectory);
  } catch (err) {
    console.error("Error creating folder:", err);
    alert("Failed to create folder:\n" + err.message);
  }
}

// Upload File
async function uploadFile() {
  const fileInput = document.getElementById("uploadInput");
  const file = fileInput.files[0];
  if (!file) return alert("Please select a file to upload.");

  const formData = new FormData();
  formData.append("path", currentdirectory);
  formData.append("file", file);

  try {
    const res = await fetch("http://localhost:8080/api/file/upload", {
      method: "POST",
      body: formData
    });

    const result = await res.text();
    if (!res.ok) throw new Error(result);

    alert(result);
    listFilesRight(currentdirectory);
  } catch (err) {
    console.error("Error uploading file:", err);
    alert("Failed to upload file:\n" + err.message);
  }
}

// Rename File/Folder
async function renameItem() {
  const oldName = prompt("Enter the name of the file to rename:");
  if (!oldName || oldName.trim() === "") return alert("Name cannot be empty.");

  const oldFullPath = `${currentdirectory}/${oldName.trim()}`;
  const newName = prompt("Enter new name for it:", oldName);
  if (!newName || newName.trim() === "") return alert("New name cannot be empty.");

  try {
    const res = await fetch("http://localhost:8080/api/file/rename", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams({
        path: currentdirectory,
        oldName: oldName.trim(),
        newName: newName.trim()
      })
    });

    const msg = await res.text();
    alert(msg);
    listFilesRight(currentdirectory);
  } catch (err) {
    console.error(err);
    alert("Rename failed.");
  }
}

// Delete File/Folder
async function deleteItem() {
  const name = prompt("Enter the name of the file to delete:");
  if (!name || name.trim() === "") return alert("Name cannot be empty.");

  const confirmed = confirm(`Are you sure you want to delete "${name.trim()}"?`);
  if (!confirmed) return;

  try {
    const res = await fetch(`http://localhost:8080/api/file/delete?path=${encodeURIComponent(currentdirectory)}&name=${encodeURIComponent(name.trim())}`, {
      method: "DELETE"
    });

    const msg = await res.text();
    alert(msg);
    listFilesRight(currentdirectory);
  } catch (err) {
    console.error(err);
    alert("Delete failed.");
  }
}

// Button bindings
document.getElementById("create_folder").addEventListener("click", createFolder);
document.getElementById("create_file").addEventListener("click", createFile);
document.getElementById("home").addEventListener("click", () => navigateToFolder(basePath));

// ✅ Double-click on folders in the left pane
document.addEventListener("dblclick", function (e) {
  const item = e.target.closest(".listf"); 
  if (item && item.parentElement.classList.contains("list")) {
    const folderName = item.querySelector("p")?.textContent.trim();
    if (folderName) {
      const path = `${basePath}/${folderName}`;
      listFilesRight(path);
    }
  }
});

// Initial load
listFiles();
listFilesRight(currentdirectory);
