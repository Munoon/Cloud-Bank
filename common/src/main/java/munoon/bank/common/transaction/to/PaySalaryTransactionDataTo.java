package munoon.bank.common.transaction.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaySalaryTransactionDataTo {
    private int userId;
    private double count;
}
