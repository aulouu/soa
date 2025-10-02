export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number; // номер текущей страницы (с 0)
  size: number;
}
