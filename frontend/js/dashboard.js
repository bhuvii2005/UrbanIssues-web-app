document.addEventListener('DOMContentLoaded', async () => {
    const grid = document.querySelector('.grid.gap-8');
    if (!grid) return;
    
    // Wipe placeholders
    grid.innerHTML = '<p class="text-slate-500 col-span-3 text-center py-10">Loading active issues...</p>';

    try {
        const issues = await fetchAPI('/issues');
        renderIssues(issues, grid);
    } catch (error) {
        grid.innerHTML = `<p class="text-error col-span-3 text-center py-10">Failed to load issues: ${error.message}</p>`;
    }
});

function renderIssues(issues, container) {
    if (issues.length === 0) {
        container.innerHTML = '<p class="text-slate-500 col-span-3 text-center py-10">No civic issues reported yet.</p>';
        return;
    }

    container.innerHTML = issues.map(issue => `
        <div class="bg-surface-container-lowest rounded-xl overflow-hidden flex flex-col group transition-all hover:shadow-[0_12px_32px_rgba(25,28,29,0.06)] h-full border border-slate-100">
            <div class="p-6 flex-1 flex flex-col">
                <div class="flex justify-between items-start mb-4">
                    <span class="text-xs font-label font-medium bg-slate-100 px-2 py-0.5 rounded">${issue.status || 'REPORTED'}</span>
                    <span class="text-[10px] font-label text-slate-400">${new Date(issue.createdAt || Date.now()).toLocaleDateString()}</span>
                </div>
                <h3 class="text-lg font-bold text-on-surface leading-tight mb-2">${issue.title}</h3>
                <p class="text-sm text-slate-500 mb-6 flex-1">${issue.description}</p>
                <div class="text-xs font-label text-slate-500 mb-4 bg-slate-50 p-2 border border-slate-100 rounded">
                    <strong>Category:</strong> ${issue.category} <br/>
                    <strong>Location:</strong> ${issue.latitude.toFixed(4)}, ${issue.longitude.toFixed(4)}
                </div>
                <div class="mt-auto flex items-center justify-between pt-4 border-t border-slate-50">
                    <div class="flex items-center gap-1 text-primary">
                        <button onclick="upvoteIssue('${issue.id}')" class="w-8 h-8 rounded-full flex items-center justify-center hover:bg-primary-fixed transition-colors">
                            <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">thumb_up</span>
                        </button>
                        <span class="text-sm font-bold" id="upvote-count-${issue.id}">${issue.upvotes}</span>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

window.upvoteIssue = async function(issueId) {
    try {
        await fetchAPI(`/issues/${issueId}/upvote`, { method: 'POST' });
        const countSpan = document.getElementById(`upvote-count-${issueId}`);
        countSpan.innerText = parseInt(countSpan.innerText) + 1;
    } catch (error) {
        alert(error.message);
    }
};
