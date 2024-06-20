package cs.vsu.ru.tpbakebudget.component;

import cs.vsu.ru.tpbakebudget.dto.response.orders.IncomeResponseDTO;
import cs.vsu.ru.tpbakebudget.enums.OrderStatus;
import cs.vsu.ru.tpbakebudget.model.Orders;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.service.impl.OrdersServiceImpl;
import cs.vsu.ru.tpbakebudget.service.impl.UsersServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReportCalculator {
    private final OrdersServiceImpl ordersService;

    private final UsersServiceImpl usersService;

    @Autowired
    public ReportCalculator(OrdersServiceImpl ordersService, UsersServiceImpl usersService) {
        this.ordersService = ordersService;
        this.usersService = usersService;
    }

    public Map<String, Integer> calculateByOrders(Long userId, LocalDate startCreatedAt, LocalDate endCreatedAt, LocalDate startFinishedAt, LocalDate endFinishedAt){
        List<Orders> createdOrders = ordersService.findByUserIdAndCreationDateBetween(userId, startCreatedAt, endCreatedAt);
        List<Orders> finishedOrders = ordersService.findByUserIdAndFinishDateBetween(userId, startFinishedAt, endFinishedAt);

        Map<String, Integer> statistics = new HashMap<>();
        int doneCount = 0;
        int notStarted = 0;
        int cancelledCount = 0;
        int inProgressCount = 0;

        for (Orders order : createdOrders) {
            switch (order.getStatus()) {
                case IN_PROCESS:
                    inProgressCount++;
                    break;
                case NOT_STARTED:
                    notStarted++;
                    break;
            }
        }

        for (Orders order : finishedOrders) {
            switch (order.getStatus()) {
                case DONE:
                    doneCount++;
                    break;
                case CANCELLED:
                    cancelledCount++;
                    break;
            }
        }

        return getStatisticsMap(statistics, doneCount, notStarted, cancelledCount, inProgressCount);
    }

    public Map<String, Integer> calculateByOrdersForGroup(String groupCode, LocalDate startCreatedAt, LocalDate endCreatedAt, LocalDate startFinishedAt, LocalDate endFinishedAt){
        List<Users> users = usersService.findAllByGroupCode(groupCode);

        Map<String, Integer> generalStatistics = new HashMap<>();
        Map<String, Integer> currentStatistics;
        int doneCount = 0;
        int notStarted = 0;
        int cancelledCount = 0;
        int inProgressCount = 0;

        for (Users user: users) {
            currentStatistics = calculateByOrders(user.getId(), startCreatedAt, endCreatedAt, startFinishedAt, endFinishedAt);
            for (Map.Entry<String, Integer> entry: currentStatistics.entrySet()) {
                switch (entry.getKey()) {
                    case "DONE":
                        doneCount += entry.getValue();
                        break;
                    case "NOT_STARTED":
                        notStarted += entry.getValue();
                        break;
                    case "CANCELLED":
                        cancelledCount += entry.getValue();
                        break;
                    case "IN_PROCESS":
                        inProgressCount += entry.getValue();
                        break;
                }
            }
        }
        return getStatisticsMap(generalStatistics, doneCount, notStarted, cancelledCount, inProgressCount);
    }

    private Map<String, Integer> getStatisticsMap(Map<String, Integer> generalStatistics, int doneCount, int notStarted, int cancelledCount, int inProgressCount) {
        generalStatistics.put(String.valueOf(OrderStatus.DONE), doneCount);
        generalStatistics.put(String.valueOf(OrderStatus.NOT_STARTED), notStarted);
        generalStatistics.put(String.valueOf(OrderStatus.CANCELLED), cancelledCount);
        generalStatistics.put(String.valueOf(OrderStatus.IN_PROCESS), inProgressCount);

        return generalStatistics;
    }

    public IncomeResponseDTO calculateByIncome(Long userId, LocalDate startCreatedAt, LocalDate endCreatedAt, LocalDate startFinishedAt, LocalDate endFinishedAt){
        List<Orders> orders = ordersService.findByUserIdAndFinishDateBetween(userId, startFinishedAt, endFinishedAt);

        double selfCost = 0;
        double income = 0;
        for (Orders order : orders) {
            if(order.getStatus() == OrderStatus.CANCELLED){
                continue;
            }
            selfCost += order.getCostPrice();
            income += order.getFinalCost();
        }

        return new IncomeResponseDTO(selfCost, income);
    }

    public IncomeResponseDTO calculateByIncomeForGroup(String groupCode, LocalDate startCreatedAt, LocalDate endCreatedAt, LocalDate startFinishedAt, LocalDate endFinishedAt){
        List<Users> users = usersService.findAllByGroupCode(groupCode);
        double cost = 0;
        double income = 0;
        IncomeResponseDTO currIncome;
        for (Users user: users) {
            currIncome = calculateByIncome(user.getId(), startCreatedAt, endCreatedAt, startFinishedAt, endFinishedAt);
            cost += currIncome.getCost();
            income += currIncome.getIncome();
        }
        return new IncomeResponseDTO(cost, income);
    }
}