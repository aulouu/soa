import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, IconButton } from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

export default function HumanBeingTable({ data, onEdit, onDelete, formatDate, sortKey, sortDirection, onSort }) {
    const renderSortArrow = (key) => sortKey === key ? (sortDirection === 'asc' ? '▲' : '▼') : '';

    return (
        <TableContainer component={Paper} elevation={1}>
            <Table sx={{ minWidth: 650 }}>
                <TableHead>
                    <TableRow sx={{ bgcolor: "primary.main" }}>
                        <TableCell
                            sx={{ color: "white", fontWeight: 600, cursor: 'pointer' }}
                            onClick={() => onSort('id')}
                        >
                            ID {renderSortArrow('id')}
                        </TableCell>
                        <TableCell
                            sx={{ color: "white", fontWeight: 600, cursor: "pointer" }}
                            onClick={() => onSort('name')}
                        >
                            Name {renderSortArrow('name')}
                        </TableCell>
                        <TableCell
                            sx={{ color: "white", fontWeight: 600, cursor: "pointer" }}
                            onClick={() => onSort('coordinates.x')}
                        >
                            X {renderSortArrow('coordinates.x')}
                        </TableCell>
                        <TableCell
                            sx={{ color: "white", fontWeight: 600, cursor: "pointer" }}
                            onClick={() => onSort('coordinates.y')}
                        >
                            Y {renderSortArrow('coordinates.y')}
                        </TableCell>
                        <TableCell
                            sx={{ color: "white", fontWeight: 600, cursor: "pointer" }}
                            onClick={() => onSort('creationDate')}
                        >
                            Creation Date {renderSortArrow('creationDate')}
                        </TableCell>
                        <TableCell
                            sx={{ color: "white", fontWeight: 600, cursor: "pointer" }}
                            onClick={() => onSort('realHero')}
                        >
                            Real Hero {renderSortArrow('realHero')}
                        </TableCell>
                        <TableCell
                            sx={{ color: "white", fontWeight: 600, cursor: "pointer" }}
                            onClick={() => onSort('hasToothpick')}
                        >
                            Has Toothpick {renderSortArrow('hasToothpick')}
                        </TableCell>
                        <TableCell
                            sx={{ color: "white", fontWeight: 600, cursor: "pointer" }}
                            onClick={() => onSort('impactSpeed')}
                        >
                            Impact Speed {renderSortArrow('impactSpeed')}
                        </TableCell>
                        <TableCell
                            sx={{ color: "white", fontWeight: 600, cursor: "pointer" }}
                            onClick={() => onSort('weaponType')}
                        >
                            Weapon Type {renderSortArrow('weaponType')}
                        </TableCell>
                        <TableCell
                            sx={{ color: "white", fontWeight: 600, cursor: "pointer" }}
                            onClick={() => onSort('mood')}
                        >
                            Mood {renderSortArrow('mood')}
                        </TableCell>
                        <TableCell
                            sx={{ color: "white", fontWeight: 600, cursor: "pointer" }}
                            onClick={() => onSort('car.cool')}
                        >
                            Car Cool {renderSortArrow('car.cool')}
                        </TableCell>
                        <TableCell sx={{ color: "white", fontWeight: 600 }} align="right"></TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {data.map((row) => (
                        <TableRow key={row.id} hover>
                            <TableCell>{row.id}</TableCell>
                            <TableCell>{row.name}</TableCell>
                            <TableCell>{row.coordinates.x}</TableCell>
                            <TableCell>{row.coordinates.y}</TableCell>
                            <TableCell>{formatDate(row.creationDate)}</TableCell>
                            <TableCell>{row.realHero ? "Yes" : "No"}</TableCell>
                            <TableCell>{row.hasToothpick ? "Yes" : "No"}</TableCell>
                            <TableCell>{row.impactSpeed}</TableCell>
                            <TableCell>{row.weaponType}</TableCell>
                            <TableCell>{row.mood}</TableCell>
                            <TableCell>{row.car?.cool ? "Yes" : "No"}</TableCell>
                            <TableCell align="right">
                                <IconButton color="primary" size="small" onClick={() => onEdit(row)} sx={{ mr: 1 }}>
                                    <EditIcon />
                                </IconButton>
                                <IconButton color="error" size="small" onClick={() => onDelete(row.id)}>
                                    <DeleteIcon />
                                </IconButton>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
}
