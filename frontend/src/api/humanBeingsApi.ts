import {Page} from "../types/api";
import {HumanBeing, HumanBeingDTO} from "../types/humanBeing";
import {apiClient} from "./axiosInstance";

type SortParams = { key: string; direction: "asc" | "desc" } | null;
type FilterParams = { [key: string]: any };

export const fetchAllHumanBeings = async (
    page = 0,
    size = 10,
    sort: SortParams = null,
    filters: FilterParams = {}
): Promise<Page<HumanBeing>> => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });
    if (sort) {
        params.append("sort", `${sort.key},${sort.direction}`);
    }
    Object.entries(filters).forEach(([key, value]) => {
        if (value !== "" && value !== null && value !== undefined) {
            params.append(key, value.toString());
        }
    });

    // ДОБАВЛЯЕМ /api/
    const response = await apiClient.get<Page<HumanBeing>>("/api/human-beings", {
        params,
    });
    return response.data;
};

export const fetchHumanBeingById = async (id: number): Promise<HumanBeing> => {
    const response = await apiClient.get<HumanBeing>(`/api/human-beings/${id}`);
    return response.data;
};

export const addHumanBeing = async (
    data: HumanBeingDTO
): Promise<HumanBeing> => {
    const response = await apiClient.post<HumanBeing>("/api/human-beings", data);
    return response.data;
};

export const updateHumanBeing = async (
    id: number,
    data: HumanBeingDTO
): Promise<HumanBeing> => {
    const response = await apiClient.put<HumanBeing>(
        `/api/human-beings/${id}`,
        data
    );
    return response.data;
};

export const deleteHumanBeing = async (id: number): Promise<void> => {
    await apiClient.delete(`/api/human-beings/${id}`);
};

// --- Статистика ---

export const countByMood = async (moodIndex: number): Promise<number> => {
    const response = await apiClient.get<number>(
        `/api/human-beings/statistics/mood-count/${moodIndex}`
    );
    return response.data;
};

export const filterByNamePrefix = async (
    prefix: string
): Promise<number> => {  // Изменили на number
    const encodedPrefix = encodeURIComponent(prefix);
    const response = await apiClient.get<number>(  // Изменили на number
        `/api/human-beings/statistics/name/starts-with/${encodedPrefix}`
    );
    return response.data;
};

export const getUniqueImpactSpeeds = async (): Promise<number[]> => {
    const response = await apiClient.get<number[]>(
        `/api/human-beings/statistics/impact-speeds`
    );
    return response.data;
};
