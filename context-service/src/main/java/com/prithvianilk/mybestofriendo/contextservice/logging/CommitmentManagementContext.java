package com.prithvianilk.mybestofriendo.contextservice.logging;

import com.prithvianilk.mybestofriendo.contextservice.model.CommitmentActionType;
import lombok.Builder;

@Builder(toBuilder = true)
public record CommitmentManagementContext(
                String participantMobileNumber,
                String messageContent,
                Integer historySnapshotSize,
                Integer futureCommitmentsSnapshotSize,
                CommitmentActionType actionType,
                Long commitmentId,
                String calendarEventId,
                Boolean success) implements Mergeable<CommitmentManagementContext> {

        @Override
        public CommitmentManagementContext merge(CommitmentManagementContext other) {
                return CommitmentManagementContext.builder()
                                .participantMobileNumber(other.participantMobileNumber() != null
                                                ? other.participantMobileNumber()
                                                : this.participantMobileNumber())
                                .messageContent(other.messageContent() != null ? other.messageContent()
                                                : this.messageContent())
                                .historySnapshotSize(other.historySnapshotSize() != null ? other.historySnapshotSize()
                                                : this.historySnapshotSize())
                                .futureCommitmentsSnapshotSize(other.futureCommitmentsSnapshotSize() != null
                                                ? other.futureCommitmentsSnapshotSize()
                                                : this.futureCommitmentsSnapshotSize())
                                .actionType(other.actionType() != null ? other.actionType() : this.actionType())
                                .commitmentId(other.commitmentId() != null ? other.commitmentId() : this.commitmentId())
                                .calendarEventId(other.calendarEventId() != null ? other.calendarEventId()
                                                : this.calendarEventId())
                                .success(other.success() != null ? other.success() : this.success())
                                .build();
        }
}
