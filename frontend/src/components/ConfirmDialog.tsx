import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle,} from "@mui/material";
import React from "react";

interface ConfirmDialogProps {
    open: boolean;
    onClose: () => void;
    onConfirm: () => void;
    title: string;
    message: string;
}

export const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
                                                                open,
                                                                onClose,
                                                                onConfirm,
                                                                title,
                                                                message,
                                                            }) => {
    return (
        <Dialog
            open={open}
            onClose={onClose}
            aria-labelledby='confirm-dialog-title'
            aria-describedby='confirm-dialog-description'
        >
            <DialogTitle id='confirm-dialog-title'>{title}</DialogTitle>
            <DialogContent>
                <DialogContentText id='confirm-dialog-description'>
                    {message}
                </DialogContentText>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Отмена</Button>
                <Button
                    onClick={onConfirm}
                    color='primary'
                    autoFocus
                >
                    Подтвердить
                </Button>
            </DialogActions>
        </Dialog>
    );
};
