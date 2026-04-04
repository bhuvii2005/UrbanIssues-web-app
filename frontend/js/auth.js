document.addEventListener('DOMContentLoaded', () => {
    
    // Auth Guard - Redirect to login if testing endpoints directly
    if (window.location.pathname.endsWith('index.html') || window.location.pathname.endsWith('report.html')) {
        if (!localStorage.getItem('token')) {
            window.location.href = "login.html";
            return;
        }
    }

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const btn = e.target.querySelector('button');
            const origText = btn.innerText;
            btn.innerText = "Loading...";

            try {
                // Backend AuthRequest takes email+password, returns AuthResponse(token)
                const res = await fetch('http://localhost:8080/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password })
                });

                if (!res.ok) throw new Error("Invalid credentials");
                const data = await res.json();
                
                localStorage.setItem('token', data.token);
                window.location.href = "index.html"; // Go to dashboard!
            } catch (error) {
                alert(error.message);
                btn.innerText = origText;
            }
        });
    }

    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const name = document.getElementById('name').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const btn = e.target.querySelector('button');
            const origText = btn.innerText;
            btn.innerText = "Loading...";

            try {
                const res = await fetch('http://localhost:8080/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name, email, password })
                });

                if (!res.ok) {
                    const errorData = await res.json();
                    throw new Error(errorData.error || "Registration failed");
                }

                // Auto-login after registration
                const loginRes = await fetch('http://localhost:8080/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password })
                });

                if (!loginRes.ok) throw new Error("Auto-login failed after registration.");
                const loginData = await loginRes.json();
                
                localStorage.setItem('token', loginData.token);
                window.location.href = "index.html";
            } catch (error) {
                alert(error.message);
                btn.innerText = origText;
            }
        });
    }
});
