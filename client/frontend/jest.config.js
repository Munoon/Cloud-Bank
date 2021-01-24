module.exports = {
    setupFilesAfterEnv: ['./src/setupJest.ts'],
    testMatch: ["**/__tests__/**/*.test.[jt]s?(x)"],
    collectCoverageFrom: ["src/**/*.[jt]s?(x)"]
};