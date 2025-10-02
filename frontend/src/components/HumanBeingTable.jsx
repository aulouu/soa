import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, IconButton } from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

export default function HumanBeingTable({ data, onEdit, onDelete, formatDate }) {
    return (
        <TableContainer component={Paper} elevation={1}>
            <Table sx={{ minWidth: 650 }}>
                <TableHead>
                    <TableRow sx={{ bgcolor: "primary.main" }}>
                        <TableCell sx={{ color: "white", fontWeight: 600 }}>ID</TableCell>
                        <TableCell sx={{ color: "white", fontWeight: 600 }}>Name</TableCell>
                        <TableCell sx={{ color: "white", fontWeight: 600 }}>X</TableCell>
                        <TableCell sx={{ color: "white", fontWeight: 600 }}>Y</TableCell>
                        <TableCell sx={{ color: "white", fontWeight: 600 }}>Creation Date</TableCell>
                        <TableCell sx={{ color: "white", fontWeight: 600 }}>Real Hero</TableCell>
                        <TableCell sx={{ color: "white", fontWeight: 600 }}>Has Toothpick</TableCell>
                        <TableCell sx={{ color: "white", fontWeight: 600 }}>Impact Speed</TableCell>
                        <TableCell sx={{ color: "white", fontWeight: 600 }}>Weapon Type</TableCell>
                        <TableCell sx={{ color: "white", fontWeight: 600 }}>Mood</TableCell>
                        <TableCell sx={{ color: "white", fontWeight: 600 }}>Car Cool</TableCell>
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
