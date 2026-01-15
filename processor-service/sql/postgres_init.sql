CREATE TABLE IF NOT EXISTS сырые_события_заказов (
    идентификатор BIGSERIAL PRIMARY KEY,
    номер_заказа VARCHAR(255) NOT NULL,
    номер_телефона_покупателя VARCHAR(255),
    описание_заказа TEXT,
    дата_события TIMESTAMP
);
