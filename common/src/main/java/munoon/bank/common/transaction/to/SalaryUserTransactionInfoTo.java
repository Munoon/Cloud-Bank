package munoon.bank.common.transaction.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import munoon.bank.common.card.CardTo;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryUserTransactionInfoTo {
    private String id;
    private CardTo card;
    private double price;
    private double actualPrice;
    private double leftBalance;
    private LocalDateTime registered;
    private boolean canceled;
}
