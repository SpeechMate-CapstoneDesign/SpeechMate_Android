
@Composable
fun Modifier.clickable(
    enabled: Boolean = true,
    isRipple: Boolean = false,
    onClick: () -> Unit,
): Modifier = composed {
    this.clickable(
        indication = if (isRipple) LocalIndication.current else null,
        interactionSource = remember { MutableInteractionSource() },
        enabled = enabled,
        onClick = onClick,
    )
}
