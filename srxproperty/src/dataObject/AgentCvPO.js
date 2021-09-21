import { ObjectUtil } from "../utils";
import { AgentAwardPO, TestimonialPO } from "../dataObject";

class AgentCvPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.aboutMe = data.aboutMe;
      this.appointment = data.appointment;
      this.awards = [];
      if (!ObjectUtil.isEmpty(data.awards)) {
        data.awards.map(item => {
          this.awards.push(new AgentAwardPO(item));
        });
      }
      this.credentialAwards = data.credentialAwards;
      this.showListings = data.showListings;
      this.showTransactions = data.showTransactions;
      this.testimonials = [];
      if (!ObjectUtil.isEmpty(data.testimonials)) {
        data.testimonials.map(item => {
          this.testimonials.push(new TestimonialPO(item));
        });
      }
    }
  }
}

export { AgentCvPO };
