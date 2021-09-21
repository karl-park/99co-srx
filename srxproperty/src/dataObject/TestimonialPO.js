import { ObjectUtil } from "../utils";

class TestimonialPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.clientName = data.clientName;
      this.id = data.id;
      this.testimonial = data.testimonial;
    }
  }
}

export { TestimonialPO };
