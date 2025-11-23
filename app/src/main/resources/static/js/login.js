async function login() {
    let user = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value,
    }

    let response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })

    switch(response.status) {
        case 200:
        {
            localStorage.setItem(
                'token',
                (await response.json()).data
            )
            document.location = '/myFiles'
        }
            break
        case 400:
            alert('Неверный логин или пароль!')
            break
    }
}
