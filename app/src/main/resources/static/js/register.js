async function register() {
    let password = document.getElementById('password').value

    let repeatPassword = document.getElementById('repeat-password').value

    if(password != repeatPassword)
        alert('Пароли не совпадают!')
    else {
        let newUser = {
            username: document.getElementById('username').value,
            password,
        }

        let response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(newUser)
        })

        switch(response.status) {
            case 201:
            {
                localStorage.setItem(
                    'token',
                    (await response.json()).data
                )

                document.location = '/myFiles'
            }
                break;
        }
    }
}
