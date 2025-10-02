"use client"

import {useEffect, useState} from "react";
import {Alert, Box, Button, Chip, CircularProgress, IconButton, Pagination, TextField, Typography} from "@mui/material";
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';

import AddIcon from "@mui/icons-material/Add";
import SearchIcon from "@mui/icons-material/Search";
import NumbersIcon from "@mui/icons-material/Numbers";
import FilterListIcon from "@mui/icons-material/FilterList";
import SpeedIcon from "@mui/icons-material/Speed";

import {
    addHumanBeing,
    countByMood,
    deleteHumanBeing,
    fetchAllHumanBeings,
    fetchHumanBeingById,
    filterByNamePrefix,
    getUniqueImpactSpeeds,
    updateHumanBeing
} from "./api/humanBeings";
import HumanBeingTable from "./components/HumanBeingTable";
import HumanBeingDialog from "./components/HumanBeingDialog";

export default function MainPage() {
    const WEAPON_TYPES = ['HAMMER', 'AXE', 'PISTOL', 'RIFLE', 'KNIFE'];
    const MOODS = ['SADNESS', 'SORROW', 'APATHY', 'FRENZY'];
    const PAGE_SIZE = 10;

    const [humanBeings, setHumanBeings] = useState([]);
    const [filteredHumanBeings, setFilteredHumanBeings] = useState([]);
    const [searchId, setSearchId] = useState("");
    const [openDialog, setOpenDialog] = useState(false);
    const [editMode, setEditMode] = useState(false);
    const [currentItem, setCurrentItem] = useState({
        id: "",
        name: "",
        coordinates: {x: "", y: ""},
        realHero: false,
        hasToothpick: false,
        impactSpeed: "",
        weaponType: "",
        mood: "",
        car: {cool: false}
    });
    const [moodValue, setMoodValue] = useState("");
    const [namePrefix, setNamePrefix] = useState("");
    const [uniqueSpeeds, setUniqueSpeeds] = useState([]);
    const [operationResult, setOperationResult] = useState(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [hasError, setHasError] = useState(false);

    useEffect(() => {
        loadHumanBeingsPage(0);
    }, []);

    const loadHumanBeingsPage = async (pageNumber = 0) => {
        setIsLoading(true);
        setHasError(false);
        try {
            const data = await fetchAllHumanBeings(pageNumber, PAGE_SIZE);
            setHumanBeings(data.content);
            setFilteredHumanBeings(data.content);
            setTotalPages(data.totalPages);
            setPage(data.number);
        } catch (error) {
            console.error(error);
            setHasError(true);
            toast.error("Error loading data from the server");
        } finally {
            setIsLoading(false);
        }
    };

    const formatDateForDisplay = (dateString) => {
        if (!dateString) return "";
        const date = new Date(dateString);
        const day = String(date.getDate()).padStart(2, "0");
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const year = date.getFullYear();
        return `${day}.${month}.${year}`;
    };

    const handleInputChange = (field, value) => {
        if (field.startsWith("coordinates.")) {
            const key = field.split(".")[1];
            setCurrentItem(prev => ({
                ...prev,
                coordinates: {...prev.coordinates, [key]: Number(value)}
            }));
        } else if (field.startsWith("car.")) {
            const key = field.split(".")[1];
            setCurrentItem(prev => ({
                ...prev,
                car: {...prev.car, [key]: value}
            }));
        } else {
            setCurrentItem(prev => ({
                ...prev,
                [field]: field === "impactSpeed" ? Number(value) : value
            }));
        }
    };

    const validateHumanBeingForm = () => {
        const {name, coordinates, impactSpeed, weaponType, mood} = currentItem;

        if (!name || !name.trim()) {
            toast.error("Name cannot be empty");
            return false;
        }
        if (!coordinates || coordinates.x === "" || coordinates.y === "") {
            toast.error("Coordinates must be filled");
            return false;
        }
        if (coordinates.y > 507) {
            toast.error("Coordinate Y must be <= 507");
            return false;
        }
        if (impactSpeed !== "" && impactSpeed < -441) {
            toast.error("Impact speed must be >= -441");
            return false;
        }
        if (weaponType && !WEAPON_TYPES.includes(weaponType)) {
            toast.error("Invalid weapon type");
            return false;
        }
        if (mood && !MOODS.includes(mood)) {
            toast.error("Invalid mood");
            return false;
        }
        return true;
    };

    const handleAdd = async () => {
        if (!validateHumanBeingForm()) return;
        try {
            await addHumanBeing({
                ...currentItem,
                coordinates: {
                    x: Number(currentItem.coordinates.x),
                    y: Number(currentItem.coordinates.y)
                },
                weaponType: currentItem.weaponType || null,
                mood: currentItem.mood || null,
                car: {cool: currentItem.car.cool}
            });
            toast.success("HumanBeing added successfully");
            handleCloseDialog();
            loadHumanBeingsPage(page);
        } catch (error) {
            toast.error(error.message);
        }
    };

    const handleUpdate = async () => {
        if (!validateHumanBeingForm()) return;
        try {
            await updateHumanBeing(currentItem.id, {
                ...currentItem,
                coordinates: {
                    x: Number(currentItem.coordinates.x),
                    y: Number(currentItem.coordinates.y)
                },
                weaponType: currentItem.weaponType || null,
                mood: currentItem.mood || null,
                car: {cool: currentItem.car.cool}
            });
            toast.success("HumanBeing updated successfully");
            handleCloseDialog();
            loadHumanBeingsPage(page);
        } catch (error) {
            toast.error(error.message);
        }
    };

    const handleDelete = async (id) => {
        try {
            await deleteHumanBeing(id);
            toast.success("HumanBeing deleted successfully");
            loadHumanBeingsPage(page);
        } catch (error) {
            toast.error(error.message);
        }
    };

    const handleOpenAddDialog = () => {
        setEditMode(false);
        setCurrentItem({
            id: "",
            name: "",
            coordinates: {x: "", y: ""},
            realHero: false,
            hasToothpick: false,
            impactSpeed: "",
            weaponType: "",
            mood: "",
            car: {cool: false}
        });
        setOpenDialog(true);
    };

    const handleOpenEditDialog = (item) => {
        setEditMode(true);
        setCurrentItem({
            id: item.id,
            name: item.name,
            coordinates: {x: item.coordinates?.x || "", y: item.coordinates?.y || ""},
            realHero: item.realHero,
            hasToothpick: item.hasToothpick,
            impactSpeed: item.impactSpeed,
            weaponType: item.weaponType,
            mood: item.mood,
            car: {cool: item.car?.cool || false}
        });
        setOpenDialog(true);
    };

    const handleCloseDialog = () => setOpenDialog(false);

    const handleSearch = async () => {
        if (!searchId.trim()) {
            setFilteredHumanBeings(humanBeings);
            return;
        }
        setIsLoading(true);
        setHasError(false);
        try {
            const data = await fetchHumanBeingById(searchId.trim());
            setFilteredHumanBeings([data]);
        } catch (error) {
            console.error(error.message);
            setFilteredHumanBeings([]);
            setHasError(true);
            toast.error("Error loading data from the server");
        } finally {
            setIsLoading(false);
        }
    };

    const handleClearSearch = () => {
        setSearchId("");
        loadHumanBeingsPage(page);
    };

    const handleCountByMood = async () => {
        if (moodValue === "" || moodValue === null) {
            toast.error("Mood value cannot be empty");
            return;
        }
        const idx = Number(moodValue);
        if (isNaN(idx) || idx < 0 || idx >= MOODS.length) {
            toast.error("Invalid mood number. Must be 0, 1, 2, 3");
            return;
        }
        setIsLoading(true);
        setHasError(false);
        try {
            const count = await countByMood(idx);
            setOperationResult({
                type: "mood",
                value: count,
                moodValueAtQuery: moodValue
            });
        } catch (error) {
            console.error(error);
            setHasError(true);
            toast.error("Error loading data from the server");
        } finally {
            setIsLoading(false);
        }
    };

    const handleFilterByName = async () => {
        if (!namePrefix.trim()) return;
        setIsLoading(true);
        setHasError(false);
        try {
            const data = await filterByNamePrefix(namePrefix);
            setFilteredHumanBeings(data);
            setOperationResult({
                type: "name",
                value: data.length,
                namePrefixAtQuery: namePrefix
            });
        } catch (error) {
            console.error(error);
            setHasError(true);
            toast.error("Error loading data from the server");
        } finally {
            setIsLoading(false);
        }
    };

    const handleClearNameFilter = () => {
        setNamePrefix("");
        loadHumanBeingsPage(page);
        setOperationResult(null);
    };

    const handleGetUniqueSpeeds = async () => {
        setIsLoading(true);
        setHasError(false);
        try {
            const data = await getUniqueImpactSpeeds();
            setUniqueSpeeds(data);
            setOperationResult({type: "speeds", value: data});
        } catch (error) {
            console.error(error);
            setHasError(true);
            toast.error("Error loading data from the server");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Box sx={{p: 4, minHeight: "100vh", bgcolor: "background.default"}}>

            <Box sx={{mb: 3, display: "flex", alignItems: "center", position: "relative"}}>
                <Typography variant="h4" sx={{color: "primary.main", fontWeight: 600}}>Human Beings</Typography>

                <Box
                    sx={{
                        display: "flex",
                        gap: 2,
                        alignItems: "center",
                        position: "absolute",
                        left: "50%",
                        transform: "translateX(-50%)"
                    }}
                >
                    <TextField
                        label="Search by ID"
                        size="small"
                        value={searchId}
                        onChange={e => setSearchId(e.target.value)}
                        onKeyPress={e => {
                            if (e.key === "Enter") handleSearch()
                        }}
                        sx={{width: 150}}
                    />
                    <IconButton
                        color="primary"
                        onClick={handleSearch}
                        sx={{bgcolor: "primary.main", color: "white", "&:hover": {bgcolor: "primary.dark"}}}
                    >
                        <SearchIcon/>
                    </IconButton>
                    {searchId && (
                        <Button variant="outlined" color="primary" onClick={handleClearSearch}
                                sx={{textTransform: "none"}}>
                            Clear
                        </Button>
                    )}
                </Box>

                <Box sx={{marginLeft: "auto"}}>
                    <Button
                        variant="contained"
                        color="primary"
                        startIcon={<AddIcon/>}
                        onClick={handleOpenAddDialog}
                        sx={{textTransform: "none"}}
                        disabled={hasError || isLoading}
                    >
                        Add New
                    </Button>
                </Box>
            </Box>

            <Box sx={{mb: 3, display: "flex", alignItems: "center", width: "100%", position: "relative"}}>
                <Box sx={{display: "flex", gap: 1, alignItems: "center"}}>
                    <TextField
                        label="Mood value"
                        size="small"
                        type="number"
                        value={moodValue}
                        onChange={(e) => {
                            const val = e.target.value;
                            if (/^-?\d*$/.test(val)) {
                                setMoodValue(val);
                            }
                        }}
                        onKeyDown={(e) => {
                            if ([".", ",", "e", "E"].includes(e.key)) {
                                e.preventDefault();
                            }
                        }}
                        inputProps={{inputMode: "numeric", pattern: "-?[0-9]*"}}
                    />
                    <IconButton
                        color="primary"
                        onClick={handleCountByMood}
                        sx={{bgcolor: "primary.main", color: "white", "&:hover": {bgcolor: "primary.dark"}}}
                        title="Count objects with mood less than value"
                    >
                        <NumbersIcon/>
                    </IconButton>
                </Box>

                <Box sx={{
                    display: "flex",
                    gap: 1,
                    alignItems: "center",
                    position: "absolute",
                    left: "50%",
                    transform: "translateX(-50%)"
                }}>
                    <TextField
                        label="Name prefix"
                        size="small"
                        value={namePrefix}
                        onChange={e => setNamePrefix(e.target.value)}
                    />
                    <IconButton
                        color="primary"
                        onClick={handleFilterByName}
                        sx={{bgcolor: "primary.main", color: "white", "&:hover": {bgcolor: "primary.dark"}}}
                        title="Filter by name prefix"
                    >
                        <FilterListIcon/>
                    </IconButton>
                    {namePrefix && (
                        <Button variant="outlined" color="primary" onClick={handleClearNameFilter}
                                sx={{textTransform: "none"}}>
                            Clear
                        </Button>
                    )}
                </Box>

                <Box sx={{display: "flex", gap: 1, alignItems: "center", marginLeft: "auto"}}>
                    <IconButton
                        color="primary"
                        onClick={handleGetUniqueSpeeds}
                        sx={{bgcolor: "primary.main", color: "white", "&:hover": {bgcolor: "primary.dark"}}}
                        title="Get unique impact speeds"
                    >
                        <SpeedIcon/>
                    </IconButton>
                    <Typography variant="body2" sx={{color: "text.secondary", whiteSpace: "nowrap"}}>
                        Unique Speeds
                    </Typography>
                </Box>
            </Box>

            {operationResult && (
                <Box sx={{mb: 2}}>
                    {operationResult.type === "mood" && (
                        <Alert severity="info" onClose={() => setOperationResult(null)}>
                            Count of objects with mood less
                            than {operationResult.moodValueAtQuery}: <strong>{operationResult.value}</strong>
                        </Alert>
                    )}
                    {operationResult.type === "name" && (
                        <Alert severity="info" onClose={() => setOperationResult(null)}>
                            Found <strong>{operationResult.value}</strong> objects with name starting with
                            "{operationResult.namePrefixAtQuery}"
                        </Alert>
                    )}
                    {operationResult.type === "speeds" && (
                        <Alert severity="info" onClose={() => setOperationResult(null)}>
                            Unique impact speeds:{" "}
                            {operationResult.value.map((speed, i) => (
                                <Chip
                                    key={i}
                                    label={speed}
                                    size="small"
                                    sx={{ml: 0.5, bgcolor: "primary.light", color: "white"}}
                                />
                            ))}
                        </Alert>
                    )}
                </Box>
            )}

            {isLoading && (
                <Box sx={{display: "flex", justifyContent: "center", my: 4}}>
                    <CircularProgress/>
                </Box>
            )}
            {hasError && (
                <Alert severity="error" sx={{my: 2}}>
                    Не удалось загрузить данные с сервера. Попробуйте снова.
                </Alert>
            )}

            {!isLoading && !hasError && (
                <HumanBeingTable
                    data={filteredHumanBeings}
                    onEdit={handleOpenEditDialog}
                    onDelete={handleDelete}
                    formatDate={formatDateForDisplay}
                />
            )}

            {!isLoading && !hasError && (
                <Box sx={{display: "flex", justifyContent: "center", mt: 2}}>
                    <Pagination
                        count={totalPages}
                        page={page + 1}
                        onChange={(e, value) => loadHumanBeingsPage(value - 1)}
                        color="primary"
                    />
                </Box>
            )}

            <HumanBeingDialog
                open={openDialog}
                onClose={handleCloseDialog}
                currentItem={currentItem}
                onChange={handleInputChange}
                onSubmit={editMode ? handleUpdate : handleAdd}
                editMode={editMode}
                WEAPON_TYPES={WEAPON_TYPES}
                MOODS={MOODS}
            />
        </Box>
    )
}
