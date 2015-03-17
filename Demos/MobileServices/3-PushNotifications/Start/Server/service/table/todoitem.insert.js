function insert(item, user, request) {
    if (item.text.length < 5) {
        request.respond(statusCodes.BAD_REQUEST, "Your todo item must be at least 5 characters");
    } else {
        request.execute();
    }

}