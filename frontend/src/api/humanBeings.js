const BASE_URL = 'http://localhost:2468/api/human-beings';
const STATISTICS_URL = 'http://localhost:2468/api/statistics';

export const fetchAllHumanBeings = async (page = 0, size = 10) => {
    const res = await fetch(`${BASE_URL}?page=${page}&size=${size}`);
    if (!res.ok) throw new Error("Failed to fetch human beings");
    return res.json();
};

export const fetchHumanBeingById = async (id) => {
    const res = await fetch(`${BASE_URL}/${id}`);
    if (!res.ok) throw new Error(`HumanBeing with id ${id} not found`);
    return res.json();
};

export const addHumanBeing = async (data) => {
    const res = await fetch(BASE_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error("Failed to add HumanBeing");
    return res.json();
};

export const updateHumanBeing = async (id, data) => {
    const res = await fetch(`${BASE_URL}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error("Failed to update HumanBeing");
    return res.json();
};

export const deleteHumanBeing = async (id) => {
    const res = await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error("Failed to delete HumanBeing");
    if (res.status === 204) { return null; }
    try {
        return await res.json();
    } catch {
        return null;
    }
};

export const countByMood = async (moodIndex) => {
    const res = await fetch(`${STATISTICS_URL}/mood-count/${moodIndex}`);
    if (!res.ok) throw new Error("Failed to count by mood");
    return res.json();
};

export const filterByNamePrefix = async (prefix) => {
    const res = await fetch(`${STATISTICS_URL}/name/starts-with/${prefix}`);
    if (!res.ok) throw new Error("Failed to filter by name");
    return res.json();
};

export const getUniqueImpactSpeeds = async () => {
    const res = await fetch(`${STATISTICS_URL}/impact-speeds`);
    if (!res.ok) throw new Error("Failed to fetch unique speeds");
    return res.json();
};
