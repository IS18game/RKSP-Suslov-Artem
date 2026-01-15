package ru.rksp.suslov.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "сырые_события_заказов")
public class RawEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "идентификатор")
    private Long идентификатор;

    @Column(name = "номер_заказа")
    private String номерЗаказа;

    @Column(name = "номер_телефона_покупателя")
    private String номерТелефонаПокупателя;

    @Column(name = "описание_заказа")
    private String описаниеЗаказа;

    @Column(name = "дата_события")
    private LocalDateTime датаСобытия;

    public Long getИдентификатор() {
        return идентификатор;
    }

    public void setИдентификатор(Long идентификатор) {
        this.идентификатор = идентификатор;
    }

    public String getНомерЗаказа() {
        return номерЗаказа;
    }

    public void setНомерЗаказа(String номерЗаказа) {
        this.номерЗаказа = номерЗаказа;
    }

    public String getНомерТелефонаПокупателя() {
        return номерТелефонаПокупателя;
    }

    public void setНомерТелефонаПокупателя(String номерТелефонаПокупателя) {
        this.номерТелефонаПокупателя = номерТелефонаПокупателя;
    }

    public String getОписаниеЗаказа() {
        return описаниеЗаказа;
    }

    public void setОписаниеЗаказа(String описаниеЗаказа) {
        this.описаниеЗаказа = описаниеЗаказа;
    }

    public LocalDateTime getДатаСобытия() {
        return датаСобытия;
    }

    public void setДатаСобытия(LocalDateTime датаСобытия) {
        this.датаСобытия = датаСобытия;
    }
}
