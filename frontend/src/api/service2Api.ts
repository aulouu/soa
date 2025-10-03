import {HumanBeing} from "../types/humanBeing";
import {service2Client} from "./axiosInstance";

export const removeWithoutToothpick = async (teamId: string): Promise<void> => {
    await service2Client.delete(`/team/${teamId}/remove-without-toothpick`);
};

export const addCarToTeam = async (teamId: string): Promise<HumanBeing[]> => {
    const response = await service2Client.post<HumanBeing[]>(
        `/team/${teamId}/car/add`,
        {}
    );
    return response.data;
};
