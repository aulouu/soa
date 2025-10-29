import axios from "axios";
import { toast } from "react-toastify";
import config from "../config";

// API клиент для human-beings-service (через Zuul Gateway)
export const apiClient = axios.create({
  baseURL: config.useGateway ? config.apiBaseUrl : config.service1Direct,
  // Браузер автоматически обрабатывает HTTPS
  // httpsAgent нужен только для Node.js окружения
});

// API клиент для heroes-service (через Zuul Gateway)
export const service2Client = axios.create({
  baseURL: config.useGateway ? config.heroesApi : `${config.service2Direct}/api/heroes`,
  // Браузер автоматически обрабатывает HTTPS
});

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
