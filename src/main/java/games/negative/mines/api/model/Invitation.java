package games.negative.mines.api.model;

import com.google.common.base.Preconditions;

import java.time.Instant;
import java.util.UUID;

public record Invitation(UUID invitee, UUID inviter, Instant timestamp) {

    public Invitation {
        Preconditions.checkNotNull(invitee, "'invitee' cannot be null");
        Preconditions.checkNotNull(inviter, "'inviter' cannot be null");
        Preconditions.checkNotNull(timestamp, "'timestamp' cannot be null");
    }
}
