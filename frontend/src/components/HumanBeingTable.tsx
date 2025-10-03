import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import DeleteIcon from "@mui/icons-material/Delete";
import DirectionsCarIcon from "@mui/icons-material/DirectionsCar";
import EditIcon from "@mui/icons-material/Edit";
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import {
    Box,
    Chip,
    IconButton,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TableSortLabel,
    Tooltip,
} from "@mui/material";
import React from "react";

import {HumanBeing} from "../types/humanBeing";

// Описываем props, которые компонент ожидает получить
interface HumanBeingTableProps {
    data: HumanBeing[];
    sortKey: string;
    sortDirection: "asc" | "desc";
    onSort: (
        key: keyof HumanBeing | "coordinates.x" | "coordinates.y" | "car.cool"
    ) => void;
    onEdit: (human: HumanBeing) => void;
    onDelete: (id: number) => void;
}

// Определяем колонки таблицы, чтобы избежать дублирования кода
const columns: {
    id: keyof HumanBeing | "coordinates.x" | "coordinates.y" | "car.cool";
    label: string;
}[] = [
    {id: "id", label: "ID"},
    {id: "name", label: "Имя"},
    {id: "coordinates.x", label: "X"},
    {id: "coordinates.y", label: "Y"},
    {id: "creationDate", label: "Дата создания"},
    {id: "realHero", label: "Герой"},
    {id: "hasToothpick", label: "Зубочистка"},
    {id: "impactSpeed", label: "Скорость"},
    {id: "weaponType", label: "Оружие"},
    {id: "mood", label: "Настроение"},
    {id: "car.cool", label: "Машина"},
    {id: "teamId", label: "ID Команды"},
];

// Функция для форматирования даты
const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("ru-RU", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
    });
};

export const HumanBeingTable: React.FC<HumanBeingTableProps> = ({
                                                                    data,
                                                                    sortKey,
                                                                    sortDirection,
                                                                    onSort,
                                                                    onEdit,
                                                                    onDelete,
                                                                }) => {
    return (
        <TableContainer
            component={Paper}
            elevation={2}
            sx={{borderRadius: 2}}
        >
            <Table sx={{minWidth: 650}}>
                <TableHead sx={{bgcolor: "primary.main"}}>
                    <TableRow>
                        {columns.map((col) => (
                            <TableCell
                                key={col.id}
                                sx={{color: "white", fontWeight: 600}}
                            >
                                <TableSortLabel
                                    active={sortKey === col.id}
                                    direction={sortKey === col.id ? sortDirection : "asc"}
                                    onClick={() => onSort(col.id)}
                                    sx={{
                                        "&.Mui-active": {color: "white"},
                                        "& .MuiTableSortLabel-icon": {color: "white !important"},
                                    }}
                                >
                                    {col.label}
                                </TableSortLabel>
                            </TableCell>
                        ))}
                        <TableCell
                            sx={{color: "white", fontWeight: 600, textAlign: "right"}}
                        >
                            Действия
                        </TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {data.map((row) => (
                        <TableRow
                            key={row.id}
                            hover
                            sx={{"&:last-child td, &:last-child th": {border: 0}}}
                        >
                            <TableCell>{row.id}</TableCell>
                            <TableCell>{row.name}</TableCell>
                            <TableCell>{row.coordinates.x}</TableCell>
                            <TableCell>{row.coordinates.y}</TableCell>
                            <TableCell>{formatDate(row.creationDate)}</TableCell>
                            <TableCell>
                                {row.realHero ? (
                                    <CheckCircleOutlineIcon color='success'/>
                                ) : (
                                    <HighlightOffIcon color='error'/>
                                )}
                            </TableCell>
                            <TableCell>
                                {row.hasToothpick ? (
                                    <CheckCircleOutlineIcon color='success'/>
                                ) : (
                                    <HighlightOffIcon color='disabled'/>
                                )}
                            </TableCell>
                            <TableCell>{row.impactSpeed}</TableCell>
                            <TableCell>{row.weaponType || "—"}</TableCell>
                            <TableCell>{row.mood || "—"}</TableCell>

                            {/* === ИЗМЕНЕННЫЙ БЛОК ДЛЯ ОТОБРАЖЕНИЯ МАШИНЫ === */}
                            <TableCell>
                                {row.car && row.car.cool === true ? (
                                    <Tooltip title='Крутая машина'>
                                        <DirectionsCarIcon color='primary'/>
                                    </Tooltip>
                                ) : row.car && row.car.cool === false ? (
                                    <Tooltip title='Обычная машина'>
                                        <DirectionsCarIcon color='disabled'/>
                                    </Tooltip>
                                ) : (
                                    "—"
                                )}
                            </TableCell>
                            {/* === КОНЕЦ ИЗМЕНЕНИЙ === */}

                            <TableCell>
                                {row.teamId ? (
                                    <Chip
                                        label={row.teamId}
                                        size='small'
                                    />
                                ) : (
                                    "—"
                                )}
                            </TableCell>
                            <TableCell align='right'>
                                <Box sx={{display: "flex", justifyContent: "flex-end"}}>
                                    <Tooltip title='Редактировать'>
                                        <IconButton
                                            color='primary'
                                            size='small'
                                            onClick={() => onEdit(row)}
                                        >
                                            <EditIcon/>
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title='Удалить'>
                                        <IconButton
                                            color='error'
                                            size='small'
                                            onClick={() => onDelete(row.id)}
                                        >
                                            <DeleteIcon/>
                                        </IconButton>
                                    </Tooltip>
                                </Box>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
};
