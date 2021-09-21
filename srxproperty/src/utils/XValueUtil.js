
import { ObjectUtil } from "./ObjectUtil";

function decryptXValueObj(obj){
  if (!ObjectUtil.isEmpty(obj)) {
    let len = obj.length;
    let i = 0;
    let result = "";

    while (i < len) {
      let x = parseInt(obj[i++], 16) << 4;
      let y = parseInt(obj[i++], 16);

      result += String.fromCharCode((x + y) ^ 0xaa);
    }
    return JSON.parse(result);
  }

  return null;
}

const XValueUtil = {
  decryptXValueObj
};

export { XValueUtil };