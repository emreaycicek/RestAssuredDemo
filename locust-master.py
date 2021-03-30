from locust import  TaskSet, task, User


class DummyTask(TaskSet):
    @task(1)
    def dummy(self):
        pass

class Dummy(User):
    task_set = DummyTask