import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Pagination,
  Paper,
  Stack,
  Typography,
} from "@mui/material";
import React, { useCallback, useEffect, useState } from "react";
import { toast } from "react-toastify";

// API
import * as humanBeingsApi from "../api/humanBeingsApi";
import * as service2Api from "../api/service2Api";

// Types
import { Page } from "../types/api";
import { HumanBeing, HumanBeingDTO } from "../types/humanBeing";

// Компоненты
import { ConfirmDialog } from "../components/ConfirmDialog";
import { HumanBeingDialog } from "../components/HumanBeingDialog";
import { HumanBeingTable } from "../components/HumanBeingTable";
import { HumanBeingToolbar } from "../components/HumanBeingToolbar";

const initialHumanBeingDTO: HumanBeingDTO = {
  name: "",
  coordinates: { x: 0, y: 0 },
  realHero: false,
  hasToothpick: false,
  impactSpeed: 0,
  weaponType: null,
  mood: null,
  car: null, // <--- ИЗМЕНЕНИЕ ЗДЕСЬ
  teamId: null,
};

const HumanBeingsPage: React.FC = () => {
  // --- Состояния ---
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [humanBeings, setHumanBeings] = useState<HumanBeing[]>([]);
  const [pageInfo, setPageInfo] = useState<Page<HumanBeing> | null>(null);
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState<{ key: string; direction: "asc" | "desc" }>({
    key: "id",
    direction: "asc",
  });
  const [filters, setFilters] = useState({});
  const [dialogOpen, setDialogOpen] = useState(false);
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [editingHuman, setEditingHuman] =
    useState<HumanBeingDTO>(initialHumanBeingDTO);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [operationResult, setOperationResult] = useState<{
    title: string;
    content: React.ReactNode;
  } | null>(null);

  // --- Функции загрузки данных ---
  const fetchData = useCallback(
    async (newFilters = filters) => {
      setIsLoading(true);
      setError(null);
      try {
        const data = await humanBeingsApi.fetchAllHumanBeings(
          page,
          10,
          sort,
          newFilters
        );
        setHumanBeings(data.content);
        setPageInfo(data);
      } catch (e: any) {
        setError("Не удалось загрузить данные с сервера.");
      } finally {
        setIsLoading(false);
      }
    },
    [page, sort, filters]
  );

  useEffect(() => {
    fetchData();
  }, [page, sort, fetchData]);

  // --- Обработчики ---
  const handleSort = (key: any) => {
    const isAsc = sort.key === key && sort.direction === "asc";
    setSort({ key, direction: isAsc ? "desc" : "asc" });
  };

  const handleAdd = () => {
    setEditingId(null);
    setEditingHuman(initialHumanBeingDTO);
    setDialogOpen(true);
  };

  const handleEdit = (human: HumanBeing) => {
    setEditingId(human.id);
    const { id, creationDate, ...dto } = human;
    setEditingHuman(dto);
    setDialogOpen(true);
  };

  const handleDelete = (id: number) => {
    setDeletingId(id);
    setConfirmDialogOpen(true);
  };

  const confirmDelete = async () => {
    if (deletingId) {
      try {
        await humanBeingsApi.deleteHumanBeing(deletingId);
        toast.success("Запись успешно удалена!");
        if (humanBeings.length === 1 && page > 0) {
          setPage(page - 1);
        } else {
          fetchData();
        }
      } finally {
        setConfirmDialogOpen(false);
        setDeletingId(null);
      }
    }
  };

  const handleSave = async (humanDTO: HumanBeingDTO) => {
    try {
      if (editingId) {
        await humanBeingsApi.updateHumanBeing(editingId, humanDTO);
        toast.success("Запись успешно обновлена!");
      } else {
        await humanBeingsApi.addHumanBeing(humanDTO);
        toast.success("Запись успешно добавлена!");
      }
      fetchData();
      setDialogOpen(false);
    } catch (e) {}
  };

  const handleApplyFilters = (newFilters: any) => {
    setPage(0);
    setFilters(newFilters);
    fetchData(newFilters);
  };

  const handleCountByMood = async (mood: string) => {
    try {
      const result = await humanBeingsApi.countByMood(parseInt(mood, 10));
      setOperationResult({
        title: "Подсчет по настроению",
        content: (
          <Typography>
            Найдено <strong>{result.count}</strong> записей с настроением{" "}
            <strong>{result.mood}</strong>.
          </Typography>
        ),
      });
    } catch (e) {}
  };

  const handleFilterByNamePrefix = async (prefix: string) => {
    try {
      const result = await humanBeingsApi.filterByNamePrefix(prefix);
      setOperationResult({
        title: `Результаты по префиксу "${prefix}"`,
        content: (
          <Typography>
            Найдено <strong>{result.count}</strong> записей, чьи имена начинаются с{" "}
            <strong>"{result.prefix}"</strong>.
          </Typography>
        ),
      });
    } catch (e) {}
  };

  const handleGetUniqueSpeeds = async () => {
    try {
      const speeds = await humanBeingsApi.getUniqueImpactSpeeds();
      setOperationResult({
        title: "Уникальные скорости",
        content: (
          <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap" }}>
            {speeds.map((s) => (
              <Chip
                key={s}
                label={s}
              />
            ))}
          </Box>
        ),
      });
    } catch (e) {}
  };

  const handleRemoveWithoutToothpick = async (teamId: string) => {
    try {
      await service2Api.removeWithoutToothpick(teamId);
      toast.success(`Из команды ${teamId} удалены герои без зубочисток!`);
      fetchData();
    } catch (e) {}
  };

  const handleAddCarToTeam = async (teamId: string) => {
    try {
      const updatedHeroes = await service2Api.addCarToTeam(teamId);
      toast.info(
        `В команде ${teamId} выданы машины ${updatedHeroes.length} героям.`
      );
      fetchData();
    } catch (e) {}
  };

  return (
    <Stack spacing={3}>
      <Typography
        variant='h4'
        component='h1'
      >
        Управление Human Beings
      </Typography>

      <HumanBeingToolbar
        onAdd={handleAdd}
        onApplyFilters={handleApplyFilters}
        onCountByMood={handleCountByMood}
        onFilterByNamePrefix={handleFilterByNamePrefix}
        onGetUniqueSpeeds={handleGetUniqueSpeeds}
        onRemoveWithoutToothpick={handleRemoveWithoutToothpick}
        onAddCarToTeam={handleAddCarToTeam}
      />

      {isLoading && (
        <Box sx={{ display: "flex", justifyContent: "center", my: 4 }}>
          <CircularProgress />
        </Box>
      )}
      {!isLoading && error && (
        <Alert
          severity='error'
          sx={{ my: 2 }}
        >
          {error}
        </Alert>
      )}

      {!isLoading && !error && (
        <Paper sx={{ p: 0, borderRadius: 2, overflow: "hidden" }}>
          <HumanBeingTable
            data={humanBeings}
            sortKey={sort.key}
            sortDirection={sort.direction}
            onSort={handleSort}
            onEdit={handleEdit}
            onDelete={handleDelete}
          />
        </Paper>
      )}

      {pageInfo && pageInfo.totalPages > 1 && !isLoading && !error && (
        <Pagination
          count={pageInfo.totalPages}
          page={page + 1}
          onChange={(e, value) => setPage(value - 1)}
          color='primary'
          sx={{ display: "flex", justifyContent: "center" }}
        />
      )}

      <Dialog
        open={!!operationResult}
        onClose={() => setOperationResult(null)}
        fullWidth
      >
        <DialogTitle>{operationResult?.title}</DialogTitle>
        <DialogContent>{operationResult?.content}</DialogContent>
        <DialogActions>
          <Button onClick={() => setOperationResult(null)}>Закрыть</Button>
        </DialogActions>
      </Dialog>

      <HumanBeingDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        onSave={handleSave}
        initialData={editingHuman}
        isEdit={!!editingId}
      />

      <ConfirmDialog
        open={confirmDialogOpen}
        onClose={() => setConfirmDialogOpen(false)}
        onConfirm={confirmDelete}
        title='Подтвердите удаление'
        message='Вы уверены, что хотите удалить эту запись? Отменить это действие будет невозможно.'
      />
    </Stack>
  );
};

export default HumanBeingsPage;
