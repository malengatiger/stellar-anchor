package com.anchor.api.data;

public enum Status {
    incomplete,
    pending_user_transfer_start,
    pending_external,
    pending_anchor,
    pending_stellar,
    pending_trust,
    pending_user,
    completed,
    no_market,
    too_small,
    too_large,
    error;
}
