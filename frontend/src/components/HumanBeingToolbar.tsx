import AddIcon from "@mui/icons-material/Add";
import ClearIcon from "@mui/icons-material/Clear";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import FilterListIcon from "@mui/icons-material/FilterList";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Button,
  IconButton,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import React, { useState } from "react";

// Описываем props, которые компонент ожидает получить
interface HumanBeingToolbarProps {
  onAdd: () => void;
  onApplyFilters: (filters: {
    name: string;
    hasToothpick: string;
    realHero: string;
  }) => void;
  onCountByMood: (mood: string) => void;
  onFilterByNamePrefix: (prefix: string) => void;
  onGetUniqueSpeeds: () => void;
  onRemoveWithoutToothpick: (teamId: string) => void;
  onAddCarToTeam: (teamId: string) => void;
}

export const HumanBeingToolbar: React.FC<HumanBeingToolbarProps> = ({
  onAdd,
  onApplyFilters,
  onCountByMood,
  onFilterByNamePrefix,
  onGetUniqueSpeeds,
  onRemoveWithoutToothpick,
  onAddCarToTeam,
}) => {
  const [filters, setFilters] = useState({
    name: "",
    hasToothpick: "",
    realHero: "",
  });
  const [mood, setMood] = useState("");
  const [namePrefix, setNamePrefix] = useState("");
  const [teamId, setTeamId] = useState("");

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilters({ ...filters, [e.target.name]: e.target.value });
  };

  const handleResetFilters = () => {
    const emptyFilters = { name: "", hasToothpick: "", realHero: "" };
    setFilters(emptyFilters);
    onApplyFilters(emptyFilters);
  };

  return (
    <Stack spacing={2}>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          flexWrap: "wrap",
          gap: 2,
        }}
      >
        <Stack
          direction='row'
          spacing={2}
          alignItems='center'
          flexWrap='wrap'
          useFlexGap
        >
          <TextField
            name='name'
            label='Фильтр по имени'
            size='small'
            value={filters.name}
            onChange={handleFilterChange}
          />
          <TextField
            name='hasToothpick'
            label='Есть зубочистка (true/false)'
            size='small'
            value={filters.hasToothpick}
            onChange={handleFilterChange}
          />
          <TextField
            name='realHero'
            label='Настоящий герой (true/false)'
            size='small'
            value={filters.realHero}
            onChange={handleFilterChange}
          />
          <Tooltip title='Применить фильтры'>
            <IconButton
              color='primary'
              onClick={() => onApplyFilters(filters)}
            >
              <FilterListIcon />
            </IconButton>
          </Tooltip>
          <Tooltip title='Сбросить фильтры'>
            <IconButton onClick={handleResetFilters}>
              <ClearIcon />
            </IconButton>
          </Tooltip>
        </Stack>
        <Button
          variant='contained'
          startIcon={<AddIcon />}
          onClick={onAdd}
        >
          Добавить
        </Button>
      </Box>

      <Accordion>
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Typography
            variant='subtitle1'
            sx={{ fontWeight: 500 }}
          >
            Специальные операции
          </Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Box sx={{ display: "flex", flexWrap: "wrap", gap: 3 }}>
            {/* Блок для Сервиса 1 */}
            <Box sx={{ width: { xs: "100%", md: "calc(50% - 12px)" } }}>
              <Typography
                variant='h6'
                gutterBottom
              >
                Статистика (Сервис 1)
              </Typography>
              <Stack
                spacing={2}
                sx={{ border: "1px solid #ddd", p: 2, borderRadius: 2 }}
              >
                <Box sx={{ display: "flex", gap: 1 }}>
                  <TextField
                    label='Индекс настроения (0-3)'
                    size='small'
                    value={mood}
                    onChange={(e) => setMood(e.target.value)}
                    fullWidth
                  />
                  <Button
                    variant='outlined'
                    onClick={() => onCountByMood(mood)}
                    disabled={!mood}
                  >
                    Посчитать
                  </Button>
                </Box>
                <Box sx={{ display: "flex", gap: 1 }}>
                  <TextField
                    label='Префикс имени'
                    size='small'
                    value={namePrefix}
                    onChange={(e) => setNamePrefix(e.target.value)}
                    fullWidth
                  />
                  <Button
                    variant='outlined'
                    onClick={() => onFilterByNamePrefix(namePrefix)}
                    disabled={!namePrefix}
                  >
                    Найти
                  </Button>
                </Box>
                <Button
                  variant='outlined'
                  onClick={onGetUniqueSpeeds}
                  fullWidth
                >
                  Получить уникальные скорости
                </Button>
              </Stack>
            </Box>

            {/* Блок для Сервиса 2 */}
            <Box sx={{ width: { xs: "100%", md: "calc(50% - 12px)" } }}>
              <Typography
                variant='h6'
                gutterBottom
              >
                Операции с командами (Сервис 2)
              </Typography>
              <Stack
                spacing={2}
                sx={{ border: "1px solid #ddd", p: 2, borderRadius: 2 }}
              >
                <TextField
                  label='ID Команды'
                  size='small'
                  value={teamId}
                  onChange={(e) => setTeamId(e.target.value)}
                />
                <Button
                  variant='outlined'
                  color='error'
                  onClick={() => onRemoveWithoutToothpick(teamId)}
                  disabled={!teamId}
                  fullWidth
                >
                  Удалить из команды без зубочисток
                </Button>
                <Button
                  variant='outlined'
                  color='secondary'
                  onClick={() => onAddCarToTeam(teamId)}
                  disabled={!teamId}
                  fullWidth
                >
                  Выдать машины членам команды
                </Button>
              </Stack>
            </Box>
          </Box>
        </AccordionDetails>
      </Accordion>
    </Stack>
  );
};
