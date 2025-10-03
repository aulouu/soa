import {
    Button,
    Checkbox,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControl,
    FormControlLabel,
    InputLabel,
    MenuItem,
    Select,
    SelectChangeEvent,
    Stack,
    TextField,
} from "@mui/material";
import React, {useEffect, useState} from "react";
import {toast} from "react-toastify";
import {HumanBeingDTO, Mood, WeaponType} from "../types/humanBeing";

interface HumanBeingDialogProps {
    open: boolean;
    onClose: () => void;
    onSave: (data: HumanBeingDTO) => void;
    initialData: HumanBeingDTO;
    isEdit: boolean;
}

const WEAPON_TYPES: WeaponType[] = [
    "AXE",
    "HAMMER",
    "KNIFE",
    "PISTOL",
    "RIFLE",
];
const MOODS: Mood[] = ["APATHY", "FRENZY", "SADNESS", "SORROW"];

type CarStatus = "none" | "regular" | "cool";

export const HumanBeingDialog: React.FC<HumanBeingDialogProps> = ({
                                                                      open,
                                                                      onClose,
                                                                      onSave,
                                                                      initialData,
                                                                      isEdit,
                                                                  }) => {
    const [formData, setFormData] = useState<HumanBeingDTO>(initialData);
    const [carStatus, setCarStatus] = useState<CarStatus>("none");

    useEffect(() => {
        setFormData(initialData);
        if (initialData.car === null || initialData.car === undefined) {
            setCarStatus("none");
        } else if (initialData.car.cool === false) {
            setCarStatus("regular");
        } else {
            setCarStatus("cool");
        }
    }, [initialData, open]);

    const handleTextChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;

        if (name === "coordinates.x" || name === "coordinates.y") {
            const [, child] = name.split(".");
            setFormData((prev) => ({
                ...prev,
                coordinates: {
                    ...prev.coordinates,
                    [child]: value === "" ? "" : Number(value),
                },
            }));
        } else {
            setFormData((prev) => ({...prev, [name]: value}));
        }
    };

    const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, checked} = e.target;
        setFormData((prev) => ({...prev, [name]: checked}));
    };

    const handleSelectChange = (e: SelectChangeEvent<string>) => {
        const {name, value} = e.target;
        setFormData((prev) => ({...prev, [name]: value || null}));
    };

    const handleCarChange = (e: SelectChangeEvent<CarStatus>) => {
        const newStatus = e.target.value as CarStatus;
        setCarStatus(newStatus);
        if (newStatus === "none") {
            setFormData((prev) => ({...prev, car: null}));
        } else if (newStatus === "regular") {
            setFormData((prev) => ({...prev, car: {cool: false}}));
        } else {
            setFormData((prev) => ({...prev, car: {cool: true}}));
        }
    };

    const validate = (): boolean => {
        if (!formData.name.trim()) {
            toast.error("Имя не может быть пустым.");
            return false;
        }
        if (formData.coordinates.y > 507) {
            toast.error("Координата Y не может быть больше 507.");
            return false;
        }
        if (formData.impactSpeed < -441) {
            toast.error("Скорость удара должна быть больше -442.");
            return false;
        }
        return true;
    };

    const handleSave = () => {
        if (validate()) {
            const dataToSave: HumanBeingDTO = {
                ...formData,
                impactSpeed: Number(formData.impactSpeed),
                teamId: formData.teamId ? Number(formData.teamId) : null,
                weaponType: formData.weaponType,
                mood: formData.mood,
            };
            onSave(dataToSave);
        }
    };

    return (
        <Dialog
            open={open}
            onClose={onClose}
            maxWidth='sm'
            fullWidth
        >
            <DialogTitle>
                {isEdit ? "Редактировать запись" : "Добавить новую запись"}
            </DialogTitle>
            <DialogContent>
                <Stack
                    spacing={2}
                    sx={{pt: 1}}
                >
                    <TextField
                        name='name'
                        label='Имя'
                        value={formData.name}
                        onChange={handleTextChange}
                        fullWidth
                        required
                    />
                    <TextField
                        name='coordinates.x'
                        label='Координата X'
                        type='number'
                        value={formData.coordinates.x}
                        onChange={handleTextChange}
                        fullWidth
                        required
                    />
                    <TextField
                        name='coordinates.y'
                        label='Координата Y'
                        type='number'
                        value={formData.coordinates.y}
                        onChange={handleTextChange}
                        fullWidth
                        required
                        error={formData.coordinates.y > 507}
                        helperText={formData.coordinates.y > 507 ? "Максимум 507" : ""}
                    />
                    <TextField
                        name='impactSpeed'
                        label='Скорость удара'
                        type='number'
                        value={formData.impactSpeed}
                        onChange={handleTextChange}
                        fullWidth
                        required
                        error={formData.impactSpeed < -441}
                        helperText={formData.impactSpeed < -441 ? "Минимум -441" : ""}
                    />
                    <TextField
                        name='teamId'
                        label='ID Команды'
                        type='number'
                        value={formData.teamId ?? ""}
                        onChange={handleTextChange}
                        fullWidth
                    />
                    <FormControl fullWidth>
                        <InputLabel id='weapon-type-label'>Оружие</InputLabel>
                        <Select
                            labelId='weapon-type-label'
                            name='weaponType'
                            value={formData.weaponType ?? ""}
                            label='Оружие'
                            onChange={handleSelectChange}
                        >
                            <MenuItem value=''>
                                <em>Не выбрано</em>
                            </MenuItem>
                            {WEAPON_TYPES.map((wt) => (
                                <MenuItem
                                    key={wt}
                                    value={wt}
                                >
                                    {wt}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth>
                        <InputLabel id='mood-label'>Настроение</InputLabel>
                        <Select
                            labelId='mood-label'
                            name='mood'
                            value={formData.mood ?? ""}
                            label='Настроение'
                            onChange={handleSelectChange}
                        >
                            <MenuItem value=''>
                                <em>Не выбрано</em>
                            </MenuItem>
                            {MOODS.map((m) => (
                                <MenuItem
                                    key={m}
                                    value={m}
                                >
                                    {m}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth>
                        <InputLabel id='car-status-label'>Машина</InputLabel>
                        <Select
                            labelId='car-status-label'
                            name='carStatus'
                            value={carStatus}
                            label='Машина'
                            onChange={handleCarChange}
                        >
                            <MenuItem value='none'>Нет машины</MenuItem>
                            <MenuItem value='regular'>Обычная машина</MenuItem>
                            <MenuItem value='cool'>Крутая машина</MenuItem>
                        </Select>
                    </FormControl>
                    <div>
                        <FormControlLabel
                            control={
                                <Checkbox
                                    name='realHero'
                                    checked={formData.realHero}
                                    onChange={handleCheckboxChange}
                                />
                            }
                            label='Герой'
                        />
                        <FormControlLabel
                            control={
                                <Checkbox
                                    name='hasToothpick'
                                    checked={formData.hasToothpick}
                                    onChange={handleCheckboxChange}
                                />
                            }
                            label='Зубочистка'
                        />
                    </div>
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Отмена</Button>
                <Button
                    onClick={handleSave}
                    variant='contained'
                >
                    {isEdit ? "Сохранить" : "Создать"}
                </Button>
            </DialogActions>
        </Dialog>
    );
};
