import { ObjectUtil } from "../utils";

class AgentAwardPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.description = data.description;
    }
  }
}

export { AgentAwardPO };
