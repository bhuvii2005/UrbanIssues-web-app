document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const titleInput = document.querySelector('input[placeholder="e.g., Large pothole on 5th Ave"]');
        const categorySelect = document.querySelector('select');
        const descInput = document.querySelector('textarea');
        
        // Mock geolocation for MVP
        const mockLat = 40.7128 + (Math.random() * 0.01 - 0.005);
        const mockLng = -74.0060 + (Math.random() * 0.01 - 0.005);

        const payload = {
            title: titleInput.value,
            category: categorySelect.value.toUpperCase(),
            description: descInput.value,
            latitude: mockLat,
            longitude: mockLng
        };

        const submitBtn = e.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerText;
        submitBtn.innerText = "Submitting...";
        submitBtn.disabled = true;

        try {
            await fetchAPI('/issues', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            alert("Issue reported successfully!");
            window.location.href = "index.html";
        } catch (error) {
            alert(`Failed: ${error.message}`);
            submitBtn.innerText = originalText;
            submitBtn.disabled = false;
        }
    });
});
