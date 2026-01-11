package com.prithvianilk.mybestofriendo.contextservice.logging;

import com.prithvianilk.mybestofriendo.contextservice.model.CommitmentActionType;
import com.prithvianilk.mybestofriendo.contextservice.model.CommitmentEntity;
import com.prithvianilk.mybestofriendo.contextservice.model.WhatsAppMessage;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record CommitmentManagementContext(
                String participantMobileNumber,
                String senderName,
                Boolean fromMe,
                String messageContent,
                Instant messageSentAt,
                Instant whatsappMessageReceivedAt,
                Integer historySnapshotSize,
                List<WhatsAppMessage> historyMessages,
                Integer futureCommitmentsSnapshotSize,
                List<CommitmentEntity> futureCommitments,
                String prompt,
                CommitmentActionType actionType,
                Long commitmentId,
                String commitmentDescription,
                Instant committedAt,
                Instant toBeCompletedAt,
                String calendarEventId,
                Boolean success,
                String failureReason,
                String validationErrors) implements Mergeable<CommitmentManagementContext> {

        @Override
        public CommitmentManagementContext merge(CommitmentManagementContext other) {
                return CommitmentManagementContext.builder()
                                .participantMobileNumber(other.participantMobileNumber() != null
                                                ? other.participantMobileNumber()
                                                : this.participantMobileNumber())
                                .senderName(other.senderName() != null ? other.senderName() : this.senderName())
                                .fromMe(other.fromMe() != null ? other.fromMe() : this.fromMe())
                                .messageContent(other.messageContent() != null ? other.messageContent()
                                                : this.messageContent())
                                .messageSentAt(other.messageSentAt() != null ? other.messageSentAt()
                                                : this.messageSentAt())
                                .whatsappMessageReceivedAt(other.whatsappMessageReceivedAt() != null
                                                ? other.whatsappMessageReceivedAt()
                                                : this.whatsappMessageReceivedAt())
                                .historySnapshotSize(other.historySnapshotSize() != null ? other.historySnapshotSize()
                                                : this.historySnapshotSize())
                                .historyMessages(other.historyMessages() != null ? other.historyMessages()
                                                : this.historyMessages())
                                .futureCommitmentsSnapshotSize(other.futureCommitmentsSnapshotSize() != null
                                                ? other.futureCommitmentsSnapshotSize()
                                                : this.futureCommitmentsSnapshotSize())
                                .futureCommitments(other.futureCommitments() != null ? other.futureCommitments()
                                                : this.futureCommitments())
                                .prompt(other.prompt() != null ? other.prompt() : this.prompt())
                                .actionType(other.actionType() != null ? other.actionType() : this.actionType())
                                .commitmentId(other.commitmentId() != null ? other.commitmentId() : this.commitmentId())
                                .commitmentDescription(other.commitmentDescription() != null
                                                ? other.commitmentDescription()
                                                : this.commitmentDescription())
                                .committedAt(other.committedAt() != null ? other.committedAt() : this.committedAt())
                                .toBeCompletedAt(other.toBeCompletedAt() != null ? other.toBeCompletedAt()
                                                : this.toBeCompletedAt())
                                .calendarEventId(other.calendarEventId() != null ? other.calendarEventId()
                                                : this.calendarEventId())
                                .success(other.success() != null ? other.success() : this.success())
                                .failureReason(other.failureReason() != null ? other.failureReason()
                                                : this.failureReason())
                                .validationErrors(other.validationErrors() != null ? other.validationErrors()
                                                : this.validationErrors())
                                .build();
        }
}
