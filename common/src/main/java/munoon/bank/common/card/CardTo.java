package munoon.bank.common.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTo {
    private String id;

    private String type;

    private String number;

    private double balance;

    private boolean active;

    private LocalDateTime registered;

    public CardTo(CardTo c) {
        this(c.getId(), c.getType(), c.getNumber(), c.getBalance(), c.isActive(), c.getRegistered());
    }
}
