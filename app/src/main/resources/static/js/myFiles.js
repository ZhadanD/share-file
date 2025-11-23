let selectedFiles = [];

async function getFiles() {
  let token = localStorage.getItem("token");

  if(!token)
    document.location = "/auth/login"

  let response = await fetch("/api/files", {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  switch (response.status) {
    case 200:
        let files = (await response.json()).data

        if(files.length == 0)
            document.getElementById('noFilesMessage').innerText = 'Файлы еще не загружены'
        else
            showFiles(files)
      break;
    case 403:
      document.location = "/auth/login"
      break;
  }
}

getFiles()

function showFiles(files) {
    files.forEach(file => {
        let downloadLink = `/api/files/download?file=${file.link}`

        document.getElementById('list-files').innerHTML += `
            <div id="file-${file.uuid}" class="file-card">
              <h3 class="file-card-header">${file.fileName}</h3>

              <div class="file-details">
                <div class="download-section">
                  <a
                    href="${downloadLink}"
                    class="download-link"
                    download
                  >
                    <span>Скачать файл</span>
                  </a>
                  <button
                    class="copy-btn"
                    onclick="copyDownloadLink('${downloadLink}')"
                  >
                    <span class="copy-text">Копировать</span>
                  </button>
                </div>

                <div class="time-section">
                  <div class="time-label">Файл удалится через:</div>
                  <div class="time-remaining" id="timer-${file.uuid}">${file.timeBeforeDeletion}</div>
                </div>
              </div>
            </div>
        `

        startFileTimer(file.uuid)
    })
}

function copyDownloadLink(url) {
    url = window.location.host + url

    navigator.clipboard.writeText(url);
}

function startFileTimer(uuid) {
    const timer = setInterval(() => {
        const timerElement = document.getElementById(`timer-${uuid}`)

        let timeBeforeDeletion = timerElement.innerText

        if (timeBeforeDeletion <= 0) {
            clearInterval(timer)

            timerElement.innerText = 'Время истекло!'

            setTimeout(() => {
                document.getElementById(`file-${uuid}`).remove()
            }, 1500)

            return
        }
        
        timeBeforeDeletion--
        
        timerElement.innerText = timeBeforeDeletion
    }, 1000)
}

function closeAccordion() {
  document.getElementById("uploadAccordion").classList.remove("active");
}

function handleFileSelect(files) {
  selectedFiles = Array.from(files);

  updateSubmitButton();

  if (selectedFiles.length > 0) showFileSelection();
}

function updateSubmitButton() {
  document.getElementById("submitBtn").disabled = selectedFiles.length === 0;
}

function showFileSelection() {
  const content = document
    .getElementById("uploadArea")
    .querySelector(".upload-area-content");

  if (content)
    content.innerHTML = `
            <p class="upload-area-subtitle">${selectedFiles[0].name}</p>
        `;
}

async function sendFile() {
    let token = localStorage.getItem('token')
    
    if(!token)
        document.location = '/auth/login'

    let file = document.getElementById('fileInput').files[0]

    let formData = new FormData()

    formData.append('file', file)
    formData.append('fileName', file.name)
    formData.append('fileSize', file.size)

    let response = await fetch('/api/files/upload', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`
        },
        body: formData
    })

    switch(response.status) {
        case 201:
            resetForm()

            closeAccordion()

            document.getElementById('noFilesMessage').innerText = ''

            let file = (await response.json()).data

            showFiles([file])
            break
        case 403:
            document.location = '/auth/login'
            break
    }
}

function resetForm() {
  selectedFiles = [];

  document.getElementById("fileInput").value = "";

  updateSubmitButton();

  const content = document
    .getElementById("uploadArea")
    .querySelector(".upload-area-content");

  if (content)
    content.innerHTML = `
            <h3 class="upload-area-title">Перетащите файл сюда</h3>
            <p class="upload-area-subtitle">или нажмите для выбора</p>
        `;
}

function uploadFiles() {
  if (selectedFiles.length === 0) return;

  selectedFiles.forEach((file) => {
    const fileInfo = {
      id: Date.now() + Math.random(),
      name: file.name,
      size: this.formatFileSize(file.size),
      type: file.type,
      date: new Date().toLocaleDateString("ru-RU"),
      timestamp: Date.now(),
    };

    this.uploadedFiles.push(fileInfo);
  });

  closeAccordion();

  resetForm();
}

function toggleAccordion() {
  document.getElementById("uploadAccordion").classList.toggle("active");
}
