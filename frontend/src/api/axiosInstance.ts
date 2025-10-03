import axios from "axios";
import {toast} from "react-toastify";

// Инстанс для первого сервиса
export const apiClient = axios.create({
    baseURL: "/", // Указываем на корень. Прокси сам добавит /api.
});

// Инстанс для второго сервиса
export const service2Client = axios.create({
    baseURL: "/s2/api/heroes",
});

// Централизованный обработчик ошибок (без изменений)
const errorHandler = (error: any) => {
    if (axios.isAxiosError(error)) {
        const errorMessage = error.response?.data?.message || error.message;
        toast.error(errorMessage);
    } else {
        toast.error("An unexpected error occurred");
    }
    return Promise.reject(error);
};

apiClient.interceptors.response.use((response) => response, errorHandler);
service2Client.interceptors.response.use((response) => response, errorHandler);
