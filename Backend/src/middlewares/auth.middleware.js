import { ApiError } from "../utils/ApiError.js";
import { asyncHandler } from "../utils/asyncHandler.js";
import jwt from "jsonwebtoken";
import { User } from "../models/user.model.js";



//res is replaced by _ , as res was not used

export const verifyJWT = asyncHandler(async (req, _, next) => {

   try {
      const token = req.cookies?.accessToken || req.header("authorization").replace("Bearer ", "");

      if (!token) {
         throw new ApiError(401, "unauthorized access");
      }

      const decodedToken = await jwt.verify(token, process.env.ACCESS_TOKEN_SECRET)

      const user = await User.findById(decodedToken?._id).select("-password -refreshToken")
      console.log(user)
      if (!user) {
         throw new ApiError(401, "Invalid User")
      }

      req.user = user
      next()
   } catch (error) {
      throw new ApiError(401, error?.message || "invalid access token")

   }
})