import axios from "axios";
import { toast } from "react-toastify";

export const apiClient = axios.create({
  baseURL: "https://localhost:37449",
});

export const service2Client = axios.create({
  baseURL: "https://localhost:37672/api/heroes",
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
