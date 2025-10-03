export type WeaponType = "HAMMER" | "AXE" | "PISTOL" | "RIFLE" | "KNIFE";
export type Mood = "SADNESS" | "SORROW" | "APATHY" | "FRENZY";

export interface Coordinates {
    x: number;
    y: number;
}

export interface Car {
    cool: boolean;
}

export interface HumanBeing {
    id: number;
    name: string;
    coordinates: Coordinates;
    creationDate: string; // В формате ISO, например "2023-10-26T10:00:00Z"
    realHero: boolean;
    hasToothpick: boolean;
    impactSpeed: number;
    weaponType: WeaponType | null;
    mood: Mood | null;
    car: Car | null;
    teamId?: number | null; // Сделаем опциональным
}

// Тип для создания/обновления. ID и creationDate не нужны.
export type HumanBeingDTO = Omit<HumanBeing, "id" | "creationDate">;
