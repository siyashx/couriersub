package com.codesupreme.couriersub.payment.service;

import com.codesupreme.couriersub.common.enums.PaymentStatus;
import com.codesupreme.couriersub.common.util.PhoneUtil;
import com.codesupreme.couriersub.payment.dto.InitPaymentRequest;
import com.codesupreme.couriersub.payment.dto.InitPaymentResponse;
import com.codesupreme.couriersub.payment.dto.PaymentResponse;
import com.codesupreme.couriersub.payment.entity.Payment;
import com.codesupreme.couriersub.payment.repo.PaymentRepository;
import com.codesupreme.couriersub.subscription.service.SubscriptionService;
import com.codesupreme.couriersub.user.entity.User;
import com.codesupreme.couriersub.user.repo.UserRepository;
import com.codesupreme.couriersub.whatsapp.group.WhatsAppGroupService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final UserRepository users;
    private final PaymentRepository payments;
    private final EpointService epoint;
    private final SubscriptionService subscriptionService;

    private final WhatsAppGroupService whatsAppGroupService;

    public PaymentService(UserRepository users,
                          PaymentRepository payments,
                          EpointService epoint,
                          SubscriptionService subscriptionService,
                          WhatsAppGroupService whatsAppGroupService) {
        this.users = users;
        this.payments = payments;
        this.epoint = epoint;
        this.subscriptionService = subscriptionService;
        this.whatsAppGroupService = whatsAppGroupService;
    }


    public InitPaymentResponse initEpoint(InitPaymentRequest req) {
        String phone = PhoneUtil.normalize(req.phone);
        User u = users.findByPhone(phone).orElseThrow(() -> new IllegalArgumentException("User tapılmadı"));

        Payment p = new Payment();
        p.setUser(u);
        p.setAmountInt(500); // 5 AZN
        p.setStatus(PaymentStatus.INITIATED);

        String providerOrderId = epoint.createProviderOrderId();
        p.setProviderOrderId(providerOrderId);

        p = payments.save(p);

        String paymentUrl = epoint.initPaymentRedirectUrl(
                providerOrderId,
                p.getAmountInt(),
                req.successUrl,
                req.errorUrl,
                req.description
        );

        InitPaymentResponse resp = new InitPaymentResponse();
        resp.paymentId = p.getId();
        resp.providerOrderId = providerOrderId;
        resp.paymentUrl = paymentUrl;
        return resp;
    }


    public void handleWebhook(String providerOrderId, String transactionId, String status) {
        Payment p = payments.findByProviderOrderId(providerOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment tapılmadı: " + providerOrderId));

        boolean ok = "SUCCESS".equalsIgnoreCase(status)
                || "success".equalsIgnoreCase(status)
                || "1".equalsIgnoreCase(status)
                || "OK".equalsIgnoreCase(status);

        if (ok) {
            p.setStatus(PaymentStatus.SUCCESS);
            p.setTransactionId(transactionId);
            p.setPaidAt(LocalDateTime.now());
            payments.save(p);

            // ✅ Subscription ACTIVE et
            subscriptionService.activateMonthly(p.getUser());
            whatsAppGroupService.addToGroup(p.getUser().getPhone());
            // ✅ Sonra WhatsApp group add burada olacaq (Mərhələ 7)
        } else {
            p.setStatus(PaymentStatus.FAILED);
            p.setTransactionId(transactionId);
            payments.save(p);
        }
    }

    public List<PaymentResponse> listMyPayments(String phone) {
        String p = PhoneUtil.normalize(phone);
        User u = users.findByPhone(p).orElseThrow(() -> new IllegalArgumentException("User tapılmadı"));

        return payments.findTop50ByUserOrderByCreatedAtDesc(u).stream().map(x -> {
            PaymentResponse r = new PaymentResponse();
            r.id = x.getId();
            r.amountInt = x.getAmountInt();
            r.currency = x.getCurrency();
            r.status = x.getStatus();
            r.providerOrderId = x.getProviderOrderId();
            r.transactionId = x.getTransactionId();
            r.createdAt = x.getCreatedAt();
            r.paidAt = x.getPaidAt();
            return r;
        }).toList();
    }

    public void handleEpointResult(String dataEncoded, String signature) {
        // 1) verify signature
        if (!epoint.verifySignature(dataEncoded, signature)) {
            throw new IllegalArgumentException("ePoint signature yanlışdır");
        }

        // 2) parse decoded json
        var result = epoint.decodeResult(dataEncoded);

        // result içindən lazım olanlar:
        // order_id, status, transaction id (olarsa)
        String orderId = result.orderId;
        String status = result.status;
        String transactionId = result.transactionId;

        // 3) DB update + subscription + group add/remove
        handleWebhook(orderId, transactionId, status);
    }

}
