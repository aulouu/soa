import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Button,
    Checkbox,
    FormControlLabel,
    MenuItem,
    Box
} from "@mui/material";

export default function HumanBeingDialog({
                                             open,
                                             onClose,
                                             currentItem,
                                             onChange,
                                             onSubmit,
                                             editMode,
                                             WEAPON_TYPES,
                                             MOODS
                                         }) {
    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>

            <DialogTitle sx={{ bgcolor: "primary.main", color: "white" }}>
                {editMode ? "Edit Human Being" : "Add New Human Being"}
            </DialogTitle>

            <DialogContent sx={{ pt: 5 }}>
                <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>

                    <TextField
                        label="Name"
                        fullWidth
                        value={currentItem.name}
                        onChange={(e) => onChange("name", e.target.value)}
                    />

                    <Box sx={{ display: "flex", gap: 2 }}>
                        <TextField
                            label="Coordinate X"
                            type="number"
                            fullWidth
                            value={currentItem.coordinates?.x || ""}
                            onChange={(e) => {
                                const val = e.target.value;
                                if (/^-?\d*$/.test(val)) {
                                    onChange("coordinates.x", val);
                                }
                            }}
                            onKeyDown={(e) => {
                                if ([".", ",", "e", "E"].includes(e.key)) {
                                    e.preventDefault();
                                }
                            }}
                            inputProps={{ inputMode: "numeric", pattern: "-?[0-9]*" }}
                        />
                        <TextField
                            label="Coordinate Y"
                            type="number"
                            fullWidth
                            value={currentItem.coordinates?.y || ""}
                            onChange={(e) => {
                                const val = e.target.value;
                                if (/^-?\d*$/.test(val)) {
                                    onChange("coordinates.y", val);
                                }
                            }}
                            onKeyDown={(e) => {
                                if ([".", ",", "e", "E"].includes(e.key)) {
                                    e.preventDefault();
                                }
                            }}
                            inputProps={{ inputMode: "numeric", pattern: "-?[0-9]*" }}
                        />
                    </Box>

                    <TextField
                        label="Impact Speed"
                        type="number"
                        fullWidth
                        value={currentItem.impactSpeed}
                        onChange={(e) => {
                            const val = e.target.value;
                            if (/^-?\d*$/.test(val)) {
                                onChange("impactSpeed", val);
                            }
                        }}
                        onKeyDown={(e) => {
                            if ([".", ",", "e", "E"].includes(e.key)) {
                                e.preventDefault();
                            }
                        }}
                        inputProps={{ inputMode: "numeric", pattern: "-?[0-9]*" }}
                    />

                    <TextField
                        select
                        label="Weapon Type"
                        fullWidth
                        value={currentItem.weaponType}
                        onChange={(e) => onChange("weaponType", e.target.value)}
                    >
                        {WEAPON_TYPES.map((type) => (
                            <MenuItem key={type} value={type}>
                                {type}
                            </MenuItem>
                        ))}
                    </TextField>

                    <TextField
                        select
                        label="Mood"
                        fullWidth
                        value={currentItem.mood}
                        onChange={(e) => onChange("mood", e.target.value)}
                    >
                        {MOODS.map((m) => (
                            <MenuItem key={m} value={m}>
                                {m}
                            </MenuItem>
                        ))}
                    </TextField>

                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={currentItem.realHero}
                                onChange={(e) => onChange("realHero", e.target.checked)}
                            />
                        }
                        label="Real Hero"
                    />

                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={currentItem.hasToothpick}
                                onChange={(e) => onChange("hasToothpick", e.target.checked)}
                            />
                        }
                        label="Has Toothpick"
                    />

                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={currentItem.car?.cool}
                                onChange={(e) => onChange("car.cool", e.target.checked)}
                            />
                        }
                        label="Car Cool"
                    />
                </Box>
            </DialogContent>

            <DialogActions sx={{ p: 2 }}>
                <Button onClick={onClose} sx={{ color: "text.secondary" }}>
                    Cancel
                </Button>
                <Button onClick={onSubmit} variant="contained" color="primary">
                    {editMode ? "Update" : "Add"}
                </Button>
            </DialogActions>

        </Dialog>
    );
}
