package kz.greetgo.sandbox.db.register_impl.migration.enums;

public enum MigrationError {
  CIA_ID_ERROR("cia_id cant be null"),
  NAME_ERROR("name cant be null"),
  SURNAME_ERROR("surname cant be null"),
  NAME_EMPTY_ERROR("name cant be empty"),
  SURNAME_EMPTY_ERROR("surname cant be empty"),
  CHARM_ERROR("charm cant be null"),
  BIRTH_NULL_ERROR("birthDate cant be null"),
  AGE_ERROR("birthDate must be not more than 100 and not less than 18"),
  DATE_INVALID_ERROR("birthDate format invalid"),

  ACCOUNT_NULL_ERROR("account number must to be not null"),
  CLIENT_ID_NULL_ERROR("client must to be not null"),
  TRANSACTION_ACCOUNT_NOT_EXIST_ERROR("transaction account not exist");

  public String message;

  @SuppressWarnings("UnnecessaryEnumModifier")
  private MigrationError(String message) {
    this.message = message;
  }
}
