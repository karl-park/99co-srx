class BusinessPO {
  constructor(data) {
    if (data) {
      this.id = data.id;
      this.name = data.name;
      this.contactCountryCode = data.contactCountryCode;
      this.contactNum = data.contactNum;
      this.address = data.address;
      this.status = data.status;
      this.logoUrl = data.logoUrl;
    }
  }
}

export {BusinessPO};
